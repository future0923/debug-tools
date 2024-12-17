package io.github.future0923.debug.tools.idea.search.utils;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import io.github.future0923.debug.tools.idea.search.HttpUrlCustomRefAnnotation;
import io.github.future0923.debug.tools.idea.search.beans.HttpUrlInfo;
import io.github.future0923.debug.tools.idea.search.enums.HttpMethod;
import io.github.future0923.debug.tools.idea.search.enums.HttpMethodSpringAnnotation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author future0923
 */
public class SpringUtils {

    /**
     * 获取Spring环境下的所有请求
     *
     * @param project 项目
     * @param module  模块
     * @return 请求集合
     */
    public static List<HttpUrlInfo> getSpringRequestByModule(Project project, Module module) {
        List<HttpUrlInfo> moduleList = new ArrayList<>(0);
        List<PsiClass> controllers = getAllControllerClass(project, module);
        if (controllers.isEmpty()) {
            return moduleList;
        }
        for (PsiClass controllerClass : controllers) {
            moduleList.addAll(getRequests(controllerClass));
        }
        return moduleList;
    }

    /**
     * 获取模块中所有的Controller和RestController的PsiClass
     *
     * @param project 项目
     * @param module  模块
     * @return PsiClass集合
     */
    private static List<PsiClass> getAllControllerClass(Project project, Module module) {
        List<PsiClass> allControllerClass = new ArrayList<>();
        GlobalSearchScope moduleScope = getModuleScope(module);
        Collection<PsiAnnotation> pathList = reflectInvokeGetAnnotations("Controller", project, moduleScope);
        pathList.addAll(reflectInvokeGetAnnotations("RestController", project, moduleScope));
        for (PsiAnnotation psiAnnotation : pathList) {
            PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
            PsiElement psiElement = psiModifierList.getParent();
            if (!(psiElement instanceof PsiClass psiClass)) {
                continue;
            }
            allControllerClass.add(psiClass);
        }
        return allControllerClass;
    }

    private static Collection<PsiAnnotation> reflectInvokeGetAnnotations(@NotNull final String s, @NotNull final Project project, @NotNull final GlobalSearchScope scope) {
        JavaAnnotationIndex javaAnnotationIndex = JavaAnnotationIndex.getInstance();
        try {
            return ReflectUtil.invoke(javaAnnotationIndex, "getAnnotations", s, project, scope);
        } catch (Exception e) {
            return ReflectUtil.invoke(javaAnnotationIndex, "get", s, project, scope);
        }
    }

    /**
     * 获取Controller下所有的请求信息
     *
     * @param controllerPsiClass 控制器PsiClass
     * @return 请求集合
     */
    public static List<HttpUrlInfo> getRequests(@NotNull PsiClass controllerPsiClass) {
        // 请求集合
        List<HttpUrlInfo> httpUrlInfos = new ArrayList<>();
        // 类上的父请求
        List<HttpUrlInfo> parentHttpUrlInfos = new ArrayList<>();
        // 方法上的子请求
        List<HttpUrlInfo> childrenHttpUrlInfos = new ArrayList<>();
        PsiAnnotation requestMappingPsiAnnotation = AttributeUtil.getClassAnnotation(
                controllerPsiClass,
                // 类上为长名字
                HttpMethodSpringAnnotation.REQUEST_MAPPING.getQualifiedName(),
                // 方法上为短名字
                HttpMethodSpringAnnotation.REQUEST_MAPPING.getShortName()
        );
        if (requestMappingPsiAnnotation != null) {
            parentHttpUrlInfos = getRequests(requestMappingPsiAnnotation, null);
        }
        PsiMethod[] psiMethods = controllerPsiClass.getAllMethods();
        for (PsiMethod psiMethod : psiMethods) {
            childrenHttpUrlInfos.addAll(getRequests(psiMethod));
        }
        if (parentHttpUrlInfos.isEmpty()) {
            httpUrlInfos.addAll(childrenHttpUrlInfos);
        } else {
            for (HttpUrlInfo parentRequestInfo : parentHttpUrlInfos) {
                for (HttpUrlInfo childrenRequestInfo : childrenHttpUrlInfos) {
                    HttpUrlInfo requestInfo = childrenRequestInfo.copyWithParent(parentRequestInfo);
                    httpUrlInfos.add(requestInfo);
                }
            }
        }
        return httpUrlInfos;
    }

    /**
     * 通过注解生成请求信息
     *
     * @param psiAnnotation psiAnnotation
     * @param psiMethod     psiMethod
     * @return 请求集合
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<HttpUrlInfo> getRequests(@NotNull PsiAnnotation psiAnnotation, @Nullable PsiMethod psiMethod) {
        HttpMethodSpringAnnotation spring = HttpMethodSpringAnnotation.getByQualifiedName(
                psiAnnotation.getQualifiedName()
        );
        if (spring == null && psiAnnotation.getResolveScope().isSearchInLibraries()) {
            spring = HttpMethodSpringAnnotation.getByShortName(psiAnnotation.getQualifiedName());
        }
        Set<HttpMethod> methods = new HashSet<>();
        List<String> paths = new ArrayList<>();
        HttpUrlCustomRefAnnotation refAnnotation = null;
        if (spring == null) {
            refAnnotation = HttpUrlCustomRefAnnotation.findCustomAnnotation(psiAnnotation);
            if (refAnnotation == null) {
                return Collections.emptyList();
            }
            methods.addAll(refAnnotation.getMethods());
        } else {
            methods.add(spring.getMethod());
        }

        // 是否为隐式的path（未定义value或者path）
        boolean hasImplicitPath = true;
        List<JvmAnnotationAttribute> attributes = psiAnnotation.getAttributes();
        for (JvmAnnotationAttribute attribute : attributes) {
            String name = attribute.getAttributeName();

            if (methods.contains(HttpMethod.REQUEST) && "method".equals(name)) {
                // method可能为数组
                Object value = AttributeUtil.getAttributeValue(attribute.getAttributeValue());
                if (value instanceof String) {
                    methods.add(HttpMethod.parse(value));
                } else if (value instanceof List) {
                    //noinspection unchecked,rawtypes
                    List<String> list = (List) value;
                    for (String item : list) {
                        if (item != null) {
                            item = item.substring(item.lastIndexOf(".") + 1);
                            methods.add(HttpMethod.parse(item));
                        }
                    }
                }
            }

            boolean flag = false;
            for (String path : new String[]{"value", "path"}) {
                if (path.equals(name)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                continue;
            }
            Object value = AttributeUtil.getAttributeValue(attribute.getAttributeValue());
            if (value instanceof String) {
                paths.add(formatPath(value));
            } else if (value instanceof List) {
                List<Object> list = (List) value;
                list.forEach(item -> paths.add(formatPath(item)));
            } else {
                throw new RuntimeException(String.format(
                        "Scan api: %s\n" +
                                "Class: %s",
                        value,
                        value != null ? value.getClass() : null
                ));
            }
            hasImplicitPath = false;
        }
        if (hasImplicitPath) {
            if (psiMethod != null) {
                List<String> loopPaths;
                if (refAnnotation != null && !(loopPaths = refAnnotation.getPaths()).isEmpty()) {
                    paths.addAll(loopPaths);
                } else {
                    paths.add("/");
                }
            }
        }

        List<HttpUrlInfo> requestInfos = new ArrayList<>(paths.size());

        paths.forEach(path -> {
            for (HttpMethod method : methods) {
                if (method.equals(HttpMethod.REQUEST) && methods.size() > 1) {
                    continue;
                }
                requestInfos.add(new HttpUrlInfo(
                        method,
                        path,
                        psiMethod
                ));
            }
        });
        return requestInfos;
    }

    /**
     * 格式化request path
     *
     * @param path path
     * @return format path
     */
    @NotNull
    @Contract(pure = true)
    public static String formatPath(@Nullable Object path) {
        if (path == null) {
            return StrUtil.SLASH;
        }
        String currPath;
        if (path instanceof String) {
            currPath = (String) path;
        } else {
            currPath = path.toString();
        }
        if (currPath.startsWith(StrUtil.SLASH)) {
            return currPath;
        }
        return StrUtil.SLASH + currPath;
    }

    private static List<HttpUrlInfo> getRequests(@NotNull PsiMethod method) {
        List<HttpUrlInfo> requestInfos = new ArrayList<>();
        for (PsiAnnotation annotation : AttributeUtil.getMethodAnnotations(method)) {
            requestInfos.addAll(getRequests(annotation, method));
        }
        return requestInfos;
    }

    /**
     * 获取模块下范围
     *
     * @param module 模块
     * @return 返回
     */
    public static GlobalSearchScope getModuleScope(Module module) {
        return getModuleScope(module, scanServiceWithLibrary(module.getProject()));
    }

    /**
     * 获取模块下范围
     *
     * @param module     模块
     * @param hasLibrary 是否扫描Library
     * @return 返回
     */
    protected static GlobalSearchScope getModuleScope(@NotNull Module module, boolean hasLibrary) {
        if (hasLibrary) {
            return module.getModuleWithLibrariesScope();
        } else {
            return module.getModuleScope();
        }
    }

    public static boolean scanServiceWithLibrary(@NotNull Project project) {
        PropertiesComponent instance = PropertiesComponent.getInstance(project);
        String value = instance.getValue(
                "SCAN_SERVICE_WITH_LIB",
                // TODO
                "false"
        );
        return Boolean.parseBoolean(value);
    }
}
