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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.getbean;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtNewMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.LoaderClassPath;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import org.springframework.cglib.core.DefaultNamingPolicy;
import org.springframework.core.SpringVersion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 代理cglib实现的Bean。JDK动态代理的实现为{@link HotswapSpringInvocationHandler}
 */
public class EnhancerProxyCreater {

    private static final Logger LOGGER = Logger.getLogger(EnhancerProxyCreater.class);
    private static EnhancerProxyCreater INSTANCE;
    public static final String SPRING_PACKAGE = "org.springframework.cglib.";
    public static final String CGLIB_PACKAGE = "net.sf.cglib.";
    public static final String CGLIB_NAME_PREFIX = "$HOTSWAPAGENT_";

    private Method createSpringProxy;
    private Method createCglibProxy;

    private final Object springLock = new Object();
    private final Object cglibLock = new Object();
    private final ClassLoader loader;
    private final ProtectionDomain protectionDomain;

    final private Map<Object, Object> beanProxies = new WeakHashMap<>();

    public EnhancerProxyCreater(ClassLoader loader, ProtectionDomain protectionDomain) {
        super();
        this.loader = loader;
        this.protectionDomain = protectionDomain;
    }

    public static boolean isSupportedCglibProxy(Object bean) {
        if (bean == null) {
            return false;
        }
        String beanClassName = bean.getClass().getName();
        return beanClassName.contains("$$EnhancerBySpringCGLIB") || beanClassName.contains("$$EnhancerByCGLIB");
    }

    /**
     * 使用cglib创建Bean代理类
     *
     * @param beanFactory  Spring beanFactory
     * @param bean         Spring bean
     * @param paramClasses Parameter Classes of the Spring beanFactory method which returned the bean. The method is named
     *                     ProxyReplacer.FACTORY_METHOD_NAME
     * @param paramValues  Parameter values of the Spring beanFactory method which returned the bean. The method is named
     *                     ProxyReplacer.FACTORY_METHOD_NAME
     * @return Bean Object
     */
    public static Object createProxy(Object beanFactory, Object bean, Class<?>[] paramClasses, Object[] paramValues) {
        if (INSTANCE == null) {
            INSTANCE = new EnhancerProxyCreater(bean.getClass().getClassLoader(), bean.getClass().getProtectionDomain());
        }
        return INSTANCE.create(beanFactory, bean, paramClasses, paramValues);
    }

    private Object create(Object beanFactory, Object bean, Class<?>[] paramClasses, Object[] paramValues) {
        Object proxyBean;
        if (beanProxies.containsKey(bean)) {
            proxyBean = beanProxies.get(bean);
        } else {
            synchronized (beanProxies) {
                if (beanProxies.containsKey(bean)) {
                    proxyBean = bean;
                } else {
                    proxyBean = doCreate(beanFactory, bean, paramClasses, paramValues);
                }
                beanProxies.put(bean, proxyBean);
            }
        }
        if (proxyBean instanceof SpringHotswapAgentProxy) {
            ((SpringHotswapAgentProxy) proxyBean).$$ha$setTarget(bean);
        }
        return proxyBean;
    }

    private Object doCreate(Object beanFactory, Object bean, Class<?>[] paramClasses, Object[] paramValues) {
        try {
            Method proxyCreater = getProxyCreationMethod(bean);
            if (proxyCreater == null) {
                return bean;
            } else {
                return proxyCreater.invoke(null, beanFactory, bean, paramClasses, paramValues);
            }
        } catch (IllegalArgumentException | InvocationTargetException e) {
            LOGGER.warning("Can't create proxy for " + bean.getClass().getSuperclass()
                    + " because there is no default constructor,"
                    + " which means your non-singleton bean created before won't get rewired with new props when update class.");
            return bean;
        } catch (IllegalAccessException | CannotCompileException | NotFoundException e) {
            LOGGER.error("Creating a proxy failed", e);
            throw new RuntimeException(e);
        }
    }

    private Method getProxyCreationMethod(Object bean) throws CannotCompileException, NotFoundException {
        ClassPool classPool = getCp(loader);
        if (classPool.find("org.springframework.cglib.proxy.MethodInterceptor") != null) {
            if (createSpringProxy == null) {
                synchronized (springLock) {
                    if (createSpringProxy == null) {
                        Class<?> springCallback = buildProxyCallbackClass(SPRING_PACKAGE, classPool);
                        Class<?> springNamingPolicy = buildNamingPolicyClass(SPRING_PACKAGE, classPool);
                        Class<?> springProxy = buildProxyCreaterClass(SPRING_PACKAGE, springCallback, springNamingPolicy, classPool);
                        createSpringProxy = springProxy.getDeclaredMethods()[0];
                    }
                }
            }
            return createSpringProxy;
        } else if (classPool.find("net.sf.cglib.proxy.MethodInterceptor") != null) {
            if (createCglibProxy == null) {
                synchronized (cglibLock) {
                    if (createCglibProxy == null) {
                        Class<?> cglibCallback = buildProxyCallbackClass(CGLIB_PACKAGE, classPool);
                        Class<?> cglibNamingPolicy = buildNamingPolicyClass(CGLIB_PACKAGE, classPool);
                        Class<?> cglibProxy = buildProxyCreaterClass(CGLIB_PACKAGE, cglibCallback, cglibNamingPolicy, classPool);
                        createCglibProxy = cglibProxy.getDeclaredMethods()[0];
                    }
                }
            }
            return createCglibProxy;
        } else {
            LOGGER.error("Unable to determine the location of the Cglib package");
            return null;
        }
    }

    /**
     * 构建一个具有单个公共静态方法 create(Object beanFactory, Object bean, Class[] classes, Object[] params) 的类。所创建类的方法返回 Cglib Enhancer 创建的参数 bean 代理。该代理具有单个回调，它是 DetachableBeanHolder 的子类。所创建代理的类名前缀将为 HOTSWAPAGENT_
     *
     * @param cglibPackage Cglib包名
     * @param callback     用于 Enhancer 的回调类
     * @param namingPolicy 用于 Enhancer 的 NamingPolicy 类
     * @param cp           javassist的ClassPool
     * @return 通过方法“public static Object create(Object beanFactory, Object bean,Class[] classes, Object[] params)”创建代理的类
     */
    public Class<?> buildProxyCreaterClass(String cglibPackage, Class<?> callback, Class<?> namingPolicy, ClassPool cp) throws CannotCompileException {
        CtClass ct = cp.makeClass("HotswapAgentSpringBeanProxy" + getClassSuffix(cglibPackage));
        String proxy = cglibPackage + "proxy.";
        String body =
                "public static Object create(Object beanFactory, Object bean, Class[] classes, Object[] params) {" +
                        callback.getName() + " handler = new " + callback.getName() + "(bean, beanFactory, classes, params);" +
                        proxy + "Enhancer e = new " + proxy + "Enhancer();" +
                        "e.setUseCache(false);" +
                        "Class[] proxyInterfaces = new Class[bean.getClass().getInterfaces().length+1];" +
                        "Class[] classInterfaces = bean.getClass().getInterfaces();" +
                        "for (int i = 0; i < classInterfaces.length; i++) {" +
                        "proxyInterfaces[i] = classInterfaces[i];" +
                        "}" +
                        "proxyInterfaces[proxyInterfaces.length-1] = io.github.future0923.debug.tools.hotswap.core.plugin.spring.getbean.SpringHotswapAgentProxy.class;" +
                        "e.setInterfaces(proxyInterfaces);" +
                        "e.setSuperclass(bean.getClass().getSuperclass());" +
                        "e.setNamingPolicy(new " + namingPolicy.getName() + "());" +
                        "e.setCallbackType(" + callback.getName() + ".class);" +
                        tryObjenesisProxyCreation(cp) +
                        "e.setCallback(handler);" +
                        "return e.create();" +
                        "}";
        CtMethod m = CtNewMethod.make(body, ct);
        ct.addMethod(m);
        return ct.toClass(loader, protectionDomain);
    }

    // Spring 4: CGLIB-based proxy classes no longer require a default constructor. Support is provided
    // via the objenesis library which is repackaged inline and distributed as part of the Spring Framework.
    // With this strategy, no constructor at all is being invoked for proxy instances anymore.
    // http://blog.codeleak.pl/2014/07/spring-4-cglib-based-proxy-classes-with-no-default-ctor.html
    //
    // If objenesis is not available (pre Spring 4), only beans with default constructor may by proxied
    private String tryObjenesisProxyCreation(ClassPool cp) {
        if (cp.find("org.springframework.objenesis.SpringObjenesis") == null) {
            return "";
        }

        // do not know why 4.2.6 AND 4.3.0 does not work, probably cglib version and cache problem
        if (SpringVersion.getVersion() == null ||
                SpringVersion.getVersion().startsWith("4.2.6") ||
                SpringVersion.getVersion().startsWith("4.3.0")) {
            return "";
        }

        return "org.springframework.objenesis.SpringObjenesis objenesis = new org.springframework.objenesis.SpringObjenesis();" +
                "if (objenesis.isWorthTrying()) {" +
                "   Class proxyClass = e.createClass();" +
                "   Object proxyInstance = objenesis.newInstance(proxyClass, false);" +
                "   ((org.springframework.cglib.proxy.Factory) proxyInstance).setCallbacks(new org.springframework.cglib.proxy.Callback[] {handler});" +
                "   return proxyInstance;" +
                "}";
    }

    /**
     * 拦截cglib的{@link DefaultNamingPolicy#getClassName}，获取名字的时候增加热重载前缀{@link #CGLIB_NAME_PREFIX}
     */
    private Class<?> buildNamingPolicyClass(String cglibPackage, ClassPool classPool) throws CannotCompileException, NotFoundException {
        CtClass ct = classPool.makeClass("HotswapAgentSpringNamingPolicy" + getClassSuffix(cglibPackage));
        String core = cglibPackage + "core.";
        String originalNamingPolicy = core + "SpringNamingPolicy";
        if (classPool.find(originalNamingPolicy) == null) {
            originalNamingPolicy = core + "DefaultNamingPolicy";
        }
        ct.setSuperclass(classPool.get(originalNamingPolicy));
        String body = "public String getClassName(String prefix, String source, Object key, " + core + "Predicate names) {" +
                "return super.getClassName(prefix + \"" + CGLIB_NAME_PREFIX + "\", source, key, names);" +
                "}";
        CtMethod m = CtNewMethod.make(body, ct);
        ct.addMethod(m);
        return ct.toClass(loader, protectionDomain);
    }

    private static String getClassSuffix(String cglibPackage) {
        return String.valueOf(cglibPackage.hashCode()).replace("-", "_");
    }

    /**
     * 创建cglib的回调类并继承{@link DetachableBeanHolder}
     */
    public Class<?> buildProxyCallbackClass(String cglibPackage, ClassPool classPool) throws CannotCompileException,
            NotFoundException {
        String proxyPackage = cglibPackage + "proxy.";
        CtClass ct = classPool.makeClass("HotswapSpringCallback" + getClassSuffix(cglibPackage));
        ct.setSuperclass(classPool.get(DetachableBeanHolder.class.getName()));
        ct.addInterface(classPool.get(proxyPackage + "MethodInterceptor"));

        String body =
                "public Object intercept(Object obj, java.lang.reflect.Method method, Object[] args, " + proxyPackage + "MethodProxy proxy) throws Throwable {" +
                        "if(method != null && method.getName().equals(\"finalize\") && method.getParameterTypes().length == 0) {" +
                        "return null;" +
                        "}" +
                        "if(method != null && method.getName().equals(\"$$ha$getTarget\")) {" +
                        "return getTarget();" +
                        "}" +
                        "if(method != null && method.getName().equals(\"$$ha$setTarget\")) {" +
                        "setTarget(args[0]); " +
                        "return null;" +
                        "}" +
                        "return proxy.invoke(getBean(), args);" +
                        "}";
        CtMethod m = CtNewMethod.make(body, ct);
        ct.addMethod(m);
        return ct.toClass(loader, protectionDomain);
    }

    private ClassPool getCp(ClassLoader loader) {
        ClassPool cp = new ClassPool();
        cp.appendSystemPath();
        cp.appendClassPath(new LoaderClassPath(loader));
        return cp;
    }


}