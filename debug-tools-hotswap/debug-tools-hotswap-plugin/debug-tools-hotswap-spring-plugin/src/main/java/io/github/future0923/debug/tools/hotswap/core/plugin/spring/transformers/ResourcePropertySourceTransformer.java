package io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtField;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;

/**
 * 对Spring的{@link ResourcePropertySource}进行转换，ResourcePropertySource可以将资源文件解析为{@link PropertySource}
 */
public class ResourcePropertySourceTransformer {

    private static final Logger LOGGER = Logger.getLogger(ResourcePropertySourceTransformer.class);

    /**
     * Insert at the beginning of the method:
     * <pre>public Set<BeanDefinition> findCandidateComponents(String basePackage)</pre>
     * new code to initialize ClassPathBeanDefinitionScannerAgent for a base class
     * It would be better to override a more appropriate method
     * org.springframework.context.annotation.ClassPathBeanDefinitionScanner.scan() directly,
     * however there are issues with javassist and varargs parameters.
     */
    @OnClassLoadEvent(classNameRegexp = "org.springframework.core.io.support.ResourcePropertySource")
    public static void transform(CtClass clazz, ClassPool classPool) throws NotFoundException, CannotCompileException {
        clazz.addInterface(classPool.get("io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.api.ReloadableResourcePropertySource"));
        clazz.addField(CtField.make("private org.springframework.core.io.support.EncodedResource encodedResource;", clazz));
        clazz.addField(CtField.make("private org.springframework.core.io.Resource resource;", clazz));
        CtConstructor ctConstructor0 = clazz.getDeclaredConstructor(new CtClass[]{classPool.get("java.lang.String"), classPool.get("org.springframework.core.io.support.EncodedResource")});
        ctConstructor0.insertBefore("this.encodedResource = $2;");
        CtConstructor ctConstructor1 = clazz.getDeclaredConstructor(new CtClass[]{classPool.get("org.springframework.core.io.support.EncodedResource")});
        ctConstructor1.insertBefore("this.encodedResource = $1;");
        CtConstructor ctConstructor2 = clazz.getDeclaredConstructor(new CtClass[]{classPool.get("java.lang.String"), classPool.get("org.springframework.core.io.Resource")});
        ctConstructor2.insertBefore("this.resource = $2;");
        CtConstructor ctConstructor3 = clazz.getDeclaredConstructor(new CtClass[]{classPool.get("org.springframework.core.io.Resource")});
        ctConstructor3.insertBefore("this.resource = $1;");
        clazz.addMethod(CtMethod.make("public org.springframework.core.io.support.EncodedResource encodedResource() { return this.encodedResource; }", clazz));
        clazz.addMethod(CtMethod.make("public org.springframework.core.io.Resource resource() { return this.resource; }", clazz));
        LOGGER.debug("class 'org.springframework.core.io.support.DefaultPropertySourceFactory' patched with PropertySource keep.");
    }
}
