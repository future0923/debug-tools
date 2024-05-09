package io.github.future0923.debug.power.idea.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DebugPowerActionUtil {

    public static String genCacheKey(PsiClass psiClass, PsiMethod psiMethod) {
        return genCacheKey(psiClass.getQualifiedName(), psiMethod.getName(), DebugPowerActionUtil.toParamTypeNameList(psiMethod.getParameterList()));
    }

    public static String genCacheKey(String className, String methodName, List<String> paramTypeNameList) {
        return className + "#" + methodName + "#" + String.join(",", paramTypeNameList);
    }

    public static List<String> toParamTypeNameList(PsiParameterList parameterList) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < parameterList.getParametersCount(); i++) {
            PsiParameter parameter = Objects.requireNonNull(parameterList.getParameter(i));
            String canonicalText = parameter.getType().getCanonicalText();
            list.add(StringUtils.substringBefore(canonicalText, "<"));
        }
        return list;
    }
}
