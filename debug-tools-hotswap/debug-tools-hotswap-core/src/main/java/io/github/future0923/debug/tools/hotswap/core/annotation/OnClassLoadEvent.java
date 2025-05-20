/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.hotswap.core.annotation;

import io.github.future0923.debug.tools.hotswap.core.annotation.handler.OnClassLoadedHandler;
import io.github.future0923.debug.tools.hotswap.core.annotation.handler.PluginAnnotation;
import io.github.future0923.debug.tools.hotswap.core.annotation.handler.PluginClassFileTransformer;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.util.HotswapTransformer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.ProtectionDomain;

import static io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent.DEFINE;
import static io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent.REDEFINE;

/**
 * 当jvm加载类时的回调方法，在define或redefine之后调用。
 * <p>该注解通过{@link OnClassLoadedHandler}来实现，创建{@link PluginClassFileTransformer}类型的Transformer调用{@link HotswapTransformer#registerTransformer}注册
 * <p>方法上可以自动注入的参数类型如下，通过{@link PluginClassFileTransformer#transform(PluginManager, PluginAnnotation, ClassLoader, String, Class, ProtectionDomain, byte[])})}解析
 * <ul>
 * <li>{@code byte[]} 输入的class字节码byte[]，不可更改
 * <li>{@link ClassLoader} 加载class的类加载器
 * <li>{@link String} 类名(e.g: {@code java/utils/List})
 * <li>{@link Class} 类被重新定义，在redefine或retransform时有值，load时为null
 * <li>{@link ProtectionDomain} 保护域
 * <li>{@link ClassPool} javassist的ClassPool
 * <li>{@link CtClass} 通过byte[]与ClassLoader创建的javassist的CtClass
 * <li>{@link LoadEvent} 加载的事件类型
 * </ul>
 * <p>{@return byte[]} 返回null表示不修改字节码，否则返回修改后的字节码byte[]
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnClassLoadEvent {

    /**
     * 感兴趣的类正则表达式
     */
    String classNameRegexp();

    /**
     * 感兴趣的类加载事件
     */
    LoadEvent[] events() default {DEFINE, REDEFINE};

    /**
     * 是否跳过主类的匿内部类（e.g:Class$1、Class$2...）
     */
    boolean skipAnonymous() default true;

    /**
     * 是否跳过运行时创建的合成类（synthetic class），agent运行时会创建新的类
     */
    boolean skipSynthetic() default true;

}
