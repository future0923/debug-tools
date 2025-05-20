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
package io.github.future0923.debug.tools.base.hutool.core.annotation;

import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解代理<br>
 * 通过代理指定注解，可以自定义调用注解的方法逻辑，如支持{@link Alias} 注解
 *
 * @param <T> 注解类型
 * @since 5.7.23
 */
public class AnnotationProxy<T extends Annotation> implements Annotation, InvocationHandler, Serializable {
	private static final long serialVersionUID = 1L;

	private final T annotation;
	private final Class<T> type;
	private final Map<String, Object> attributes;

	/**
	 * 构造
	 *
	 * @param annotation 注解
	 */
	@SuppressWarnings("unchecked")
	public AnnotationProxy(T annotation) {
		this.annotation = annotation;
		this.type = (Class<T>) annotation.annotationType();
		this.attributes = initAttributes();
	}


	@Override
	public Class<? extends Annotation> annotationType() {
		return type;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		// 注解别名
		Alias alias = method.getAnnotation(Alias.class);
		if(null != alias){
			final String name = alias.value();
			if(StrUtil.isNotBlank(name)){
				if(false == attributes.containsKey(name)){
					throw new IllegalArgumentException(StrUtil.format("No method for alias: [{}]", name));
				}
				return attributes.get(name);
			}
		}

		final Object value = attributes.get(method.getName());
		if (value != null) {
			return value;
		}
		return method.invoke(this, args);
	}

	/**
	 * 初始化注解的属性<br>
	 * 此方法预先调用所有注解的方法，将注解方法值缓存于attributes中
	 *
	 * @return 属性（方法结果）映射
	 */
	private Map<String, Object> initAttributes() {
		final Method[] methods = ReflectUtil.getMethods(this.type);
		final Map<String, Object> attributes = new HashMap<>(methods.length, 1);

		for (Method method : methods) {
			// 跳过匿名内部类自动生成的方法
			if (method.isSynthetic()) {
				continue;
			}

			attributes.put(method.getName(), ReflectUtil.invoke(this.annotation, method));
		}

		return attributes;
	}
}
