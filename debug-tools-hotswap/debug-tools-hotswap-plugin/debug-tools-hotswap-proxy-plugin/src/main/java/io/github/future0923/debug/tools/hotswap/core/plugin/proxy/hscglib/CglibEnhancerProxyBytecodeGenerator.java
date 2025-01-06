package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib;

import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.ProxyBytecodeGenerator;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 为 Cglib 增强器代理创建新的字节码。使用加载了 ParentLastClassLoader 新实例的类。
 *
 * @author future0923
 */
public class CglibEnhancerProxyBytecodeGenerator implements ProxyBytecodeGenerator {

    private final GeneratorParams param;
    private final ClassLoader classLoader;
    private final Class<?> generatorClass;
    private final Object generator;
    private final Class<?> abstractGeneratorClass;

    public CglibEnhancerProxyBytecodeGenerator(GeneratorParams param,
                                               ClassLoader classLoader) {
        this.param = param;
        this.classLoader = new ParentLastClassLoader(classLoader);
        this.generator = param.getParam();
        this.generatorClass = generator.getClass();
        this.abstractGeneratorClass = generatorClass.getSuperclass();
    }

    private static class FieldState {

        private final Field field;
        private final Object fieldValue;

        public FieldState(Field field, Object fieldValue) {
            this.field = field;
            this.fieldValue = fieldValue;
        }
    }

    @Override
    public byte[] generate() throws Exception {
        Collection<FieldState> oldClassValues = getFieldValuesWithClasses();
        ClassLoader oldClassLoader = (ClassLoader) ReflectionHelper.get(generator, "classLoader");
        Boolean oldUseCache = (Boolean) ReflectionHelper.get(generator, "useCache");
        try {
            ReflectionHelper.set(generator, abstractGeneratorClass, "classLoader", classLoader);
            ReflectionHelper.set(generator, abstractGeneratorClass, "useCache", Boolean.FALSE);
            setFieldValuesWithNewLoadedClasses(oldClassValues);
            return (byte[]) ReflectionHelper.invoke(param.getGenerator(), param.getGenerator().getClass(), "generate", new Class[] { getGeneratorInterfaceClass() }, generator);
        } finally {
            ReflectionHelper.set(generator, abstractGeneratorClass, "classLoader", oldClassLoader);
            ReflectionHelper.set(generator, abstractGeneratorClass, "useCache", oldUseCache);
            setFieldValues(oldClassValues);
        }
    }

    private Class<?> getGeneratorInterfaceClass() {
        Class<?>[] interfaces = abstractGeneratorClass.getInterfaces();
        for (Class<?> iClass : interfaces) {
            if (iClass.getName().endsWith(".ClassGenerator")) {
                return iClass;
            }
        }
        return null;
    }

    private void setFieldValues(Collection<FieldState> fieldStates)
            throws IllegalAccessException {
        for (FieldState fieldState : fieldStates) {
            fieldState.field.set(generator, fieldState.fieldValue);
        }
    }

    /**
     * 用ParentLastClassLoader加载的新类替换类值字段
     */
    private void setFieldValuesWithNewLoadedClasses(
            Collection<FieldState> fieldStates)
            throws IllegalAccessException, ClassNotFoundException {
        for (FieldState fieldState : fieldStates) {
            fieldState.field.set(generator, loadFromClassloader(fieldState.fieldValue));
        }
    }

    private Collection<FieldState> getFieldValuesWithClasses()
            throws IllegalAccessException {
        Collection<FieldState> classValueFields = new ArrayList<FieldState>();

        Field[] fields = generatorClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())
                    && (field.getType().isInstance(Class.class)
                    || field.getType().isInstance(Class[].class))) {
                field.setAccessible(true);
                classValueFields
                        .add(new FieldState(field, field.get(generator)));
            }
        }
        return classValueFields;
    }

    private Object loadFromClassloader(Object fieldState)
            throws ClassNotFoundException {
        if (fieldState instanceof Class[]) {
            Class<?>[] classes = ((Class[]) fieldState);
            Class<?>[] newClasses = new Class[classes.length];
            for (int i = 0; i < classes.length; i++) {
                Class<?> loadClass = classLoader
                        .loadClass(classes[i].getName());
                newClasses[i] = loadClass;
            }
            return newClasses;
        } else {
            if (fieldState instanceof Class) {
                return classLoader.loadClass(((Class<?>) fieldState).getName());
            }
        }
        return fieldState;
    }
}
