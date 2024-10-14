package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Objects;

public class DebugToolsIdeaClassUtil {

    public static String getMethodQualifiedName(PsiMethod psiMethod) {
        // 获取方法所在的Psi类， 在代码分析、重构和导航时非常有用，因为它允许你获取方法所属的类，从而可以执行各种操作，比如检查类的属性、调用其他方法等。
        PsiClass containingClass = psiMethod.getContainingClass();
        if (containingClass != null) {
            StringBuilder fullQualifiedName = new StringBuilder(containingClass.getQualifiedName() + "#" + psiMethod.getName());
            PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
            if (parameters.length > 0) {
                fullQualifiedName.append("(");
                for (int i = 0; i < parameters.length; i++) {
                    fullQualifiedName.append(parameters[i].getType().getCanonicalText());
                    if (i < parameters.length - 1) {
                        fullQualifiedName.append(",");
                    }
                }
                fullQualifiedName.append(")");
            }
            return fullQualifiedName.toString();
        } else {
            return psiMethod.getName();
        }
    }

    public static String getSimpleMethodName(String qualifiedMethodName) {
        String methodName = qualifiedMethodName.substring(qualifiedMethodName.lastIndexOf("#") + 1);
        if (methodName.contains("(")) {
            return methodName.substring(0, methodName.indexOf("("));
        }
        return methodName;

    }

    /**
     * 在给定的项目中查找指定名称的 Java 类
     *
     * @param project            搜索项目
     * @param qualifiedClassName 类标识符
     * @return PsiClass信息
     */
    public static PsiClass findClass(Project project, String qualifiedClassName) {
        return JavaPsiFacade.getInstance(project).findClass(qualifiedClassName, GlobalSearchScope.allScope(project));
    }

    /**
     * 在给定的项目中查找指定名称的 Method 类
     *
     * @param project             搜索项目
     * @param qualifiedMethodName 方法标识符
     * @return PsiMethod信息
     */
    public static PsiMethod findMethod(Project project, String qualifiedMethodName) {
        PsiClass psiClass = findClass(project, qualifiedMethodName.substring(0, qualifiedMethodName.lastIndexOf("#")));
        if (Objects.nonNull(psiClass)) {
            PsiMethod[] methods = psiClass.findMethodsByName(getSimpleMethodName(qualifiedMethodName), false);
            for (PsiMethod method : methods) {
                if (Objects.equals(getMethodQualifiedName(method), qualifiedMethodName)) {
                    return method;
                }
            }
        }
        return null;
    }
}
