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
 * <p>{@link Link}的子注解。表示“原始属性”将作为“关联属性”的别名。
 * <ul>
 *     <li>当“原始属性”为默认值时，获取“关联属性”将返回“关联属性”本身的值；</li>
 *     <li>当“原始属性”不为默认值时，获取“关联属性”将返回“原始属性”的值；</li>
 * </ul>
 * <b>注意，该注解与{@link Link}、{@link ForceAliasFor}或{@link MirrorFor}一起使用时，将只有被声明在最上面的注解会生效</b>
 *
 * @author huangchengxing
 * @see Link
 * @see RelationType#ALIAS_FOR
 */
@Link(type = RelationType.ALIAS_FOR)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface AliasFor {

	/**
	 * 产生关联的注解类型，当不指定时，默认指注释的属性所在的类
	 *
	 * @return 注解类型
	 */
	@Link(annotation = Link.class, attribute = "annotation", type = RelationType.FORCE_ALIAS_FOR)
	Class<? extends Annotation> annotation() default Annotation.class;

	/**
	 * {@link #annotation()}指定注解中关联的属性
	 *
	 * @return 关联属性
	 */
	@Link(annotation = Link.class, attribute = "attribute", type = RelationType.FORCE_ALIAS_FOR)
	String attribute() default "";

}
