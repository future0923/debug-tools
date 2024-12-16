package io.github.future0923.debug.tools.idea.api;

import com.intellij.lang.jvm.annotation.JvmAnnotationArrayValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationEnumFieldValue;
import com.intellij.psi.PsiAnnotation;
import io.github.future0923.debug.tools.idea.api.enums.HttpMethod;
import io.github.future0923.debug.tools.idea.api.enums.SpringHttpMethodAnnotation;
import io.github.future0923.debug.tools.idea.api.utils.AttributeUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author future0923
 */
@Getter
public class CustomRefAnnotation {

    private final List<String> paths;
    private final List<HttpMethod> methods;

    public CustomRefAnnotation() {
        this.paths = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    public void addPath(@NotNull String... paths) {
        if (paths.length < 1) {
            return;
        }
        this.paths.addAll(Arrays.asList(paths));
    }

    public void addMethods(@NotNull HttpMethod... methods) {
        if (methods.length < 1) {
            return;
        }
        this.methods.addAll(Arrays.asList(methods));
    }

    @Nullable
    public static CustomRefAnnotation findCustomAnnotation(@NotNull PsiAnnotation psiAnnotation) {
        PsiAnnotation qualifiedAnnotation = AttributeUtil.getQualifiedAnnotation(
                psiAnnotation,
                SpringHttpMethodAnnotation.REQUEST_MAPPING.getQualifiedName()
        );
        if (qualifiedAnnotation == null) {
            return null;
        }
        CustomRefAnnotation otherAnnotation = new CustomRefAnnotation();

        for (JvmAnnotationAttribute attribute : qualifiedAnnotation.getAttributes()) {
            Object methodValues = getAnnotationValue(attribute, "method");
            if (methodValues != null) {
                List<?> methods = methodValues instanceof List ? ((List<?>) methodValues) : Collections.singletonList(methodValues);
                if (methods.isEmpty()) {
                    continue;
                }
                for (Object method : methods) {
                    if (method == null) {
                        continue;
                    }
                    otherAnnotation.addMethods(HttpMethod.parse(method));
                }
                continue;
            }

            Object pathValues = getAnnotationValue(attribute, "path", "value");
            if (pathValues != null) {
                List<?> paths = pathValues instanceof List ? ((List<?>) pathValues) : Collections.singletonList(pathValues);
                if (!paths.isEmpty()) {
                    for (Object path : paths) {
                        if (path == null) {
                            continue;
                        }
                        otherAnnotation.addPath((String) path);
                    }
                }
            }
        }
        return otherAnnotation;
    }

    private static Object getAnnotationValue(@NotNull JvmAnnotationAttribute attribute, @NotNull String... attrNames) {
        String attributeName = attribute.getAttributeName();
        if (attrNames.length < 1) {
            return null;
        }
        boolean matchAttrName = false;
        for (String attrName : attrNames) {
            if (attributeName.equals(attrName)) {
                matchAttrName = true;
                break;
            }
        }
        if (!matchAttrName) {
            return null;
        }
        JvmAnnotationAttributeValue attributeValue = attribute.getAttributeValue();
        return getAttributeValue(attributeValue);
    }

    private static Object getAttributeValue(@Nullable JvmAnnotationAttributeValue attributeValue) {
        if (attributeValue == null) {
            return null;
        }
        if (attributeValue instanceof JvmAnnotationConstantValue) {
            Object constantValue = ((JvmAnnotationConstantValue) attributeValue).getConstantValue();
            return constantValue == null ? null : constantValue.toString();
        } else if (attributeValue instanceof JvmAnnotationEnumFieldValue) {
            return ((JvmAnnotationEnumFieldValue) attributeValue).getFieldName();
        } else if (attributeValue instanceof JvmAnnotationArrayValue) {
            List<String> values = new ArrayList<>();
            for (JvmAnnotationAttributeValue value : ((JvmAnnotationArrayValue) attributeValue).getValues()) {
                values.add((String) getAttributeValue(value));
            }
            return values;
        }
        return null;
    }
}
