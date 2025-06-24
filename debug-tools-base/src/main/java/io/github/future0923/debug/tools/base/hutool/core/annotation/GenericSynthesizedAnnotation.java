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
package io.github.future0923.debug.tools.base.hutool.core.annotation;

import io.github.future0923.debug.tools.base.hutool.core.lang.Opt;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link SynthesizedAnnotation}的基本实现
 *
 * @param <R> 根对象类型
 * @param <T> 注解类型
 * @author huangchengxing
 */
public class GenericSynthesizedAnnotation<R, T extends Annotation> implements SynthesizedAnnotation {

	private final R root;
	private final T annotation;
	private final Map<String, AnnotationAttribute> attributeMethodCaches;
	private final int verticalDistance;
	private final int horizontalDistance;

	/**
	 * 创建一个合成注解
	 *
	 * @param root               根对象
	 * @param annotation         被合成的注解对象
	 * @param verticalDistance   距离根对象的水平距离
	 * @param horizontalDistance 距离根对象的垂直距离
	 */
	protected GenericSynthesizedAnnotation(
		R root, T annotation, int verticalDistance, int horizontalDistance) {
		this.root = root;
		this.annotation = annotation;
		this.verticalDistance = verticalDistance;
		this.horizontalDistance = horizontalDistance;
		this.attributeMethodCaches = new HashMap<>();
		this.attributeMethodCaches.putAll(loadAttributeMethods());
	}

	/**
	 * 加载注解属性
	 *
	 * @return 注解属性
	 */
	protected Map<String, AnnotationAttribute> loadAttributeMethods() {
		return Stream.of(ClassUtil.getDeclaredMethods(annotation.annotationType()))
			.filter(AnnotationUtil::isAttributeMethod)
			.collect(Collectors.toMap(Method::getName, method -> new CacheableAnnotationAttribute(annotation, method)));
	}

	/**
	 * 元注解是否存在该属性
	 *
	 * @param attributeName 属性名
	 * @return 是否存在该属性
	 */
	public boolean hasAttribute(String attributeName) {
		return attributeMethodCaches.containsKey(attributeName);
	}

	/**
	 * 元注解是否存在该属性，且该属性的值类型是指定类型或其子类
	 *
	 * @param attributeName 属性名
	 * @param returnType    返回值类型
	 * @return 是否存在该属性
	 */
	@Override
	public boolean hasAttribute(String attributeName, Class<?> returnType) {
		return Opt.ofNullable(attributeMethodCaches.get(attributeName))
			.filter(method -> ClassUtil.isAssignable(returnType, method.getAttributeType()))
			.isPresent();
	}

	/**
	 * 获取该注解的全部属性
	 *
	 * @return 注解属性
	 */
	@Override
	public Map<String, AnnotationAttribute> getAttributes() {
		return this.attributeMethodCaches;
	}

	/**
	 * 设置属性值
	 *
	 * @param attributeName 属性名称
	 * @param attribute     注解属性
	 */
	@Override
	public void setAttribute(String attributeName, AnnotationAttribute attribute) {
		attributeMethodCaches.put(attributeName, attribute);
	}

	/**
	 * 替换属性值
	 *
	 * @param attributeName 属性名
	 * @param operator      替换操作
	 */
	@Override
	public void replaceAttribute(String attributeName, UnaryOperator<AnnotationAttribute> operator) {
		AnnotationAttribute old = attributeMethodCaches.get(attributeName);
		if (ObjectUtil.isNotNull(old)) {
			attributeMethodCaches.put(attributeName, operator.apply(old));
		}
	}

	/**
	 * 获取属性值
	 *
	 * @param attributeName 属性名
	 * @return 属性值
	 */
	@Override
	public Object getAttributeValue(String attributeName) {
		return Opt.ofNullable(attributeMethodCaches.get(attributeName))
			.map(AnnotationAttribute::getValue)
			.get();
	}

	/**
	 * 获取该合成注解对应的根节点
	 *
	 * @return 合成注解对应的根节点
	 */
	@Override
	public R getRoot() {
		return root;
	}

	/**
	 * 获取被合成的注解对象
	 *
	 * @return 注解对象
	 */
	@Override
	public T getAnnotation() {
		return annotation;
	}

	/**
	 * 获取该合成注解与根对象的垂直距离。
	 * 默认情况下，该距离即为当前注解与根对象之间相隔的层级数。
	 *
	 * @return 合成注解与根对象的垂直距离
	 */
	@Override
	public int getVerticalDistance() {
		return verticalDistance;
	}

	/**
	 * 获取该合成注解与根对象的水平距离。
	 * 默认情况下，该距离即为当前注解与根对象之间相隔的已经被扫描到的注解数。
	 *
	 * @return 合成注解与根对象的水平距离
	 */
	@Override
	public int getHorizontalDistance() {
		return horizontalDistance;
	}

	/**
	 * 获取被合成的注解类型
	 *
	 * @return 被合成的注解类型
	 */
	@Override
	public Class<? extends Annotation> annotationType() {
		return annotation.annotationType();
	}

	/**
	 * 获取注解属性值
	 *
	 * @param attributeName  属性名称
	 * @param attributeType  属性类型
	 * @return 注解属性值
	 */
	@Override
	public Object getAttributeValue(String attributeName, Class<?> attributeType) {
		return Opt.ofNullable(attributeMethodCaches.get(attributeName))
			.filter(method -> ClassUtil.isAssignable(attributeType, method.getAttributeType()))
			.map(AnnotationAttribute::getValue)
			.get();
	}
}
