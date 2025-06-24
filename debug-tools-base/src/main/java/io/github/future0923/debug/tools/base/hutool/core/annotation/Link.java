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


import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>用于在同一注解中，或具有一定关联的不同注解的属性中，表明这些属性之间具有特定的关联关系。
 * 在通过{@link SynthesizedAggregateAnnotation}获取合成注解后，合成注解获取属性值时会根据该注解进行调整。<br>
 *
 * <p>该注解存在三个字注解：{@link MirrorFor}、{@link ForceAliasFor}或{@link AliasFor}，
 * 使用三个子注解等同于{@link Link}。但是需要注意的是，
 * 当注解中的属性同时存在多个{@link Link}或基于{@link Link}的子注解时，
 * 仅有声明在被注解的属性最上方的注解会生效，其余注解都将被忽略。
 *
 * <b>注意：该注解的优先级低于{@link Alias}</b>
 *
 * @author huangchengxing
 * @see SynthesizedAggregateAnnotation
 * @see RelationType
 * @see AliasFor
 * @see MirrorFor
 * @see ForceAliasFor
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Link {

	/**
	 * 产生关联的注解类型，当不指定时，默认指注释的属性所在的类
	 *
	 * @return 关联的注解类型
	 */
	Class<? extends Annotation> annotation() default Annotation.class;

	/**
	 * {@link #annotation()}指定注解中关联的属性
	 *
	 * @return 属性名
	 */
	String attribute() default "";

	/**
	 * {@link #attribute()}指定属性与当前注解的属性建的关联关系类型
	 *
	 * @return 关系类型
	 */
	RelationType type() default RelationType.MIRROR_FOR;

}
