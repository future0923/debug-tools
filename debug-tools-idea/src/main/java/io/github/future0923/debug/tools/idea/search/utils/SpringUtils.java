/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.idea.search.utils;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJvmMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.idea.search.HttpUrlCustomRefAnnotation;
import io.github.future0923.debug.tools.idea.search.beans.HttpUrlItem;
import io.github.future0923.debug.tools.idea.search.enums.HttpMethod;
import io.github.future0923.debug.tools.idea.search.enums.HttpMethodSpringAnnotation;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author future0923
 */
public class SpringUtils {
    private static final Set<String> CONTROLLER_ANNOTATIONS = Set.of(
            "org.springframework.web.bind.annotation.RestController",
            "org.springframework.stereotype.Controller"
    );

    /**
     * 获取Spring环境下的所有请求
     *
     * @param project 项目
     * @param module  模块
     * @return 请求集合
     */
    public static List<HttpUrlItem> getSpringRequestByModule(Project project, Module module) {
        List<HttpUrlItem> moduleList = new ArrayList<>(0);
        List<PsiClass> controllers = getAllControllerClass(project, module);
        if (controllers.isEmpty()) {
            return moduleList;
        }
        for (PsiClass controllerClass : controllers) {
            moduleList.addAll(getRequests(controllerClass, module));
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
        GlobalSearchScope moduleScope = getModuleScope(project, module);
        Collection<String> annotationNames = JavaAnnotationIndex.getInstance().getAllKeys(project);
        Set<String> controllerAnnotationNames = new HashSet<>();

        for (String annotationName : annotationNames) {
            Collection<PsiAnnotation> annotations = JavaAnnotationIndex.getInstance().get(annotationName, project, moduleScope);
            for (PsiAnnotation usage : annotations) {
                PsiJavaCodeReferenceElement ref = usage.getNameReferenceElement();
                if (ref == null) continue;

                PsiElement resolved = ref.resolve();
                if (!(resolved instanceof PsiClass annoClass)) continue;

                if (!annoClass.isAnnotationType()) continue;

                if (isControllerAnnotation(annoClass)) {
                    String qualifiedName = annoClass.getQualifiedName();
                    if (qualifiedName != null) {
                        controllerAnnotationNames.add(qualifiedName);
                    }
                }
            }
        }

        for (String annotationName : controllerAnnotationNames) {
            String annotationShortName = getShortName(annotationName);

            Collection<PsiAnnotation> usages = JavaAnnotationIndex.getInstance().getAnnotations(annotationShortName, project, moduleScope);

            for (PsiAnnotation usage : usages) {
                PsiElement owner = usage.getParent().getParent();
                if (owner instanceof PsiClass psiClass) {
                    allControllerClass.add(psiClass);
                }
            }
        }

        return allControllerClass;
    }

    private static boolean isControllerAnnotation(PsiClass annoClass) {
        PsiModifierList modifierList = annoClass.getModifierList();
        if (modifierList == null) return false;

        for (PsiAnnotation annotation : modifierList.getAnnotations()) {
            if (hasControllerMeta(annotation, new HashSet<>())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasControllerMeta(PsiAnnotation annotation, Set<String> visited) {
        String qName = annotation.getQualifiedName();
        if (qName == null || !visited.add(qName)) {
            return false;
        }

        if (CONTROLLER_ANNOTATIONS.contains(qName)) {
            return true;
        }

        PsiJavaCodeReferenceElement ref = annotation.getNameReferenceElement();
        if (ref == null) return false;

        PsiElement resolved = ref.resolve();
        if (!(resolved instanceof PsiClass annoClass)) return false;

        PsiModifierList modifierList = annoClass.getModifierList();
        if (modifierList == null) return false;

        for (PsiAnnotation meta : modifierList.getAnnotations()) {
            if (hasControllerMeta(meta, visited)) {
                return true;
            }
        }
        return false;
    }

    public static String getShortName(String qualifiedName) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    }

    /**
     * 获取Controller下所有的请求信息
     *
     * @param controllerPsiClass 控制器PsiClass
     * @return 请求集合
     */
    public static List<HttpUrlItem> getRequests(@NotNull PsiClass controllerPsiClass, Module module) {
        // 请求集合
        List<HttpUrlItem> httpUrlInfos = new ArrayList<>();
        // 类上的父请求
        List<HttpUrlItem> parentHttpUrlInfos = new ArrayList<>();
        // 方法上的子请求
        List<HttpUrlItem> childrenHttpUrlInfos = new ArrayList<>();
        PsiAnnotation requestMappingPsiAnnotation = AttributeUtil.getClassAnnotation(
                controllerPsiClass,
                // 类上为长名字
                HttpMethodSpringAnnotation.REQUEST_MAPPING.getQualifiedName(),
                // 方法上为短名字
                HttpMethodSpringAnnotation.REQUEST_MAPPING.getShortName()
        );
        if (requestMappingPsiAnnotation != null) {
            parentHttpUrlInfos = getRequests(requestMappingPsiAnnotation, null, module);
        }
        PsiMethod[] psiMethods = controllerPsiClass.getAllMethods();
        for (PsiMethod psiMethod : psiMethods) {
            childrenHttpUrlInfos.addAll(getRequests(psiMethod, module));
        }
        if (parentHttpUrlInfos.isEmpty()) {
            httpUrlInfos.addAll(childrenHttpUrlInfos);
        } else {
            for (HttpUrlItem parentRequestInfo : parentHttpUrlInfos) {
                for (HttpUrlItem childrenRequestInfo : childrenHttpUrlInfos) {
                    HttpUrlItem requestInfo = childrenRequestInfo.copyWithParent(parentRequestInfo);
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
    private static List<HttpUrlItem> getRequests(@NotNull PsiAnnotation psiAnnotation, @Nullable PsiMethod psiMethod, Module module) {
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
            if (value == null) {
                continue;
            }
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
                        value.getClass()
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

        List<HttpUrlItem> requestInfos = new ArrayList<>(paths.size());
        String className = Optional.ofNullable(psiMethod).map(PsiJvmMember::getContainingClass).map(NavigationItem::getName).orElse(null);
        String methodName = Optional.ofNullable(psiMethod).map(NavigationItem::getName).orElse(null);
        String comment = Optional.ofNullable(psiMethod).map(method -> getDocComment(psiMethod)).orElse(null);
        paths.forEach(path -> {
            for (HttpMethod method : methods) {
                if (method.equals(HttpMethod.REQUEST) && methods.size() > 1) {
                    continue;
                }
                requestInfos.add(new HttpUrlItem(
                        psiMethod,
                        method,
                        path,
                        module.getName(),
                        className,
                        methodName,
                        comment
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

    private static List<HttpUrlItem> getRequests(@NotNull PsiMethod method, Module module) {
        List<HttpUrlItem> requestInfos = new ArrayList<>();
        for (PsiAnnotation annotation : AttributeUtil.getMethodAnnotations(method)) {
            requestInfos.addAll(getRequests(annotation, method, module));
        }
        return requestInfos;
    }

    /**
     * 获取模块下范围
     *
     * @param module 模块
     * @return 返回
     */
    protected static GlobalSearchScope getModuleScope(Project project, @NotNull Module module) {
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        if (BooleanUtil.isTrue(settingState.getSearchLibrary())) {
            return module.getModuleWithLibrariesScope();
        } else {
            return module.getModuleContentScope();
        }
    }

    private static String getDocComment(PsiMethod psiMethod) {
        String comment = null;
        PsiDocComment docComment = psiMethod.getDocComment();
        if (docComment != null) {

            PsiElement[] descriptionElements = docComment.getDescriptionElements();
            comment = Arrays.stream(descriptionElements)
                    .map(PsiElement::getText)
                    .collect(Collectors.joining())
                    .replace("*", "")
                    .trim();
        }
        return comment;
    }
}
