/*
 * Copyright 2013-2024 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
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
