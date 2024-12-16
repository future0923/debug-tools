package io.github.future0923.debug.tools.idea.api.utils;

import com.intellij.lang.jvm.annotation.JvmAnnotationArrayValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationClassValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationEnumFieldValue;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author future0923
 */
public class AttributeUtil {

    /**
     * 获取属性值
     *
     * @param attributeValue Psi属性
     * @return {Object | List}
     */
    @Nullable
    public static Object getAttributeValue(JvmAnnotationAttributeValue attributeValue) {
        if (attributeValue == null) {
            return null;
        }
        if (attributeValue instanceof JvmAnnotationConstantValue) {
            return ((JvmAnnotationConstantValue) attributeValue).getConstantValue();
        } else if (attributeValue instanceof JvmAnnotationEnumFieldValue) {
            return ((JvmAnnotationEnumFieldValue) attributeValue).getFieldName();
        } else if (attributeValue instanceof JvmAnnotationArrayValue) {
            List<JvmAnnotationAttributeValue> values = ((JvmAnnotationArrayValue) attributeValue).getValues();
            List<Object> list = new ArrayList<>(values.size());
            for (JvmAnnotationAttributeValue value : values) {
                Object o = getAttributeValue(value);
                if (o != null) {
                    list.add(o);
                } else {
                    // 如果是jar包里的JvmAnnotationConstantValue则无法正常获取值
                    try {
                        Class<? extends JvmAnnotationAttributeValue> clazz = value.getClass();
                        Field myElement = clazz.getSuperclass().getDeclaredField("myElement");
                        myElement.setAccessible(true);
                        Object elObj = myElement.get(value);
                        if (elObj instanceof PsiExpression expression) {
                            list.add(expression.getText());
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
            return list;
        } else if (attributeValue instanceof JvmAnnotationClassValue) {
            return ((JvmAnnotationClassValue) attributeValue).getQualifiedName();
        }
        return null;
    }

    /**
     * 查找类上的指定注解（包括超类和接口）
     *
     * @param psiClass      PsiClass
     * @param qualifiedName 注解全限定名
     * @return PsiAnnotation
     */
    @Nullable
    public static PsiAnnotation getClassAnnotation(@NotNull PsiClass psiClass, @NotNull String... qualifiedName) {
        if (qualifiedName.length < 1) {
            return null;
        }
        PsiAnnotation annotation;
        for (String name : qualifiedName) {
            annotation = psiClass.getAnnotation(name);
            if (annotation != null) {
                return annotation;
            }
        }
        List<PsiClass> classes = new ArrayList<>();
        classes.add(psiClass.getSuperClass());
        classes.addAll(Arrays.asList(psiClass.getInterfaces()));
        for (PsiClass superPsiClass : classes) {
            if (superPsiClass == null) {
                continue;
            }
            PsiAnnotation classAnnotation = getClassAnnotation(superPsiClass, qualifiedName);
            if (classAnnotation != null) {
                return classAnnotation;
            }
        }
        return null;
    }

    /**
     * 获取方法的所有注解（包括父类方法）
     *
     * @param psiMethod psiMethod
     * @return PsiAnnotation集合
     */
    @NotNull
    public static List<PsiAnnotation> getMethodAnnotations(@NotNull PsiMethod psiMethod) {
        List<PsiAnnotation> annotations = new ArrayList<>(Arrays.asList(psiMethod.getModifierList().getAnnotations()));
        for (PsiMethod superMethod : psiMethod.findSuperMethods()) {
            getMethodAnnotations(superMethod)
                    .stream()
                    // 筛选：子类中方法定义了父类中方法存在的注解时只保留最上层的注解（即实现类的方法注解
                    .filter(annotation -> !annotations.contains(annotation))
                    .forEach(annotations::add);
        }
        return annotations;
    }

    @Nullable
    public static PsiAnnotation getQualifiedAnnotation(PsiAnnotation psiAnnotation, @NotNull String qualifiedName) {
        final String targetAnn = "java.lang.annotation.Target";
        final String documentedAnn = "java.lang.annotation.Documented";
        final String retentionAnn = "java.lang.annotation.Retention";
        if (psiAnnotation == null) {
            return null;
        }
        String annotationQualifiedName = psiAnnotation.getQualifiedName();
        if (qualifiedName.equals(annotationQualifiedName)) {
            return psiAnnotation;
        }
        if (targetAnn.equals(annotationQualifiedName) || documentedAnn.equals(annotationQualifiedName) || retentionAnn.equals(annotationQualifiedName)) {
            return null;
        }
        PsiJavaCodeReferenceElement element = psiAnnotation.getNameReferenceElement();
        if (element == null) {
            return null;
        }
        PsiElement resolve = element.resolve();
        if (!(resolve instanceof PsiClass psiClass)) {
            return null;
        }
        if (!psiClass.isAnnotationType()) {
            return null;
        }
        PsiAnnotation annotation = psiClass.getAnnotation(qualifiedName);
        if (annotation != null && qualifiedName.equals(annotation.getQualifiedName())) {
            return annotation;
        }
        for (PsiAnnotation classAnnotation : psiClass.getAnnotations()) {
            PsiAnnotation qualifiedAnnotation = getQualifiedAnnotation(classAnnotation, qualifiedName);
            if (qualifiedAnnotation != null) {
                return qualifiedAnnotation;
            }
        }
        return null;
    }
}
