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


/**
 * <p>注解属性的关系类型 <br>
 * 若将被{@link Link}注解的属性称为“原始属性”，而在{@link Link}注解中指向的注解属性称为“关联属性”，
 * 则该枚举用于描述“原始属性”与“关联属性”在{@link SynthesizedAggregateAnnotation}处理过程中的作用关系。<br>
 * 根据在{@link Link#type()}中指定的关系类型的不同，通过{@link SynthesizedAggregateAnnotation}合成的注解的属性值也将有所变化。
 *
 * <p>当一个注解中的所有属性同时具备多种关系时，将依次按下述顺序处理：
 * <ol>
 *     <li>属性上的{@link Alias}注解；</li>
 *     <li>属性上的{@link Link}注解，且{@link Link#type()}为{@link #MIRROR_FOR}；</li>
 *     <li>属性上的{@link Link}注解，且{@link Link#type()}为{@link #FORCE_ALIAS_FOR}；</li>
 *     <li>属性上的{@link Link}注解，且{@link Link#type()}为{@link #ALIAS_FOR}；</li>
 * </ol>
 *
 * @author huangchengxing
 * @see SynthesizedAggregateAnnotation
 * @see Link
 */
public enum RelationType {

	/**
	 * <p>表示注解的属性与指定的属性互为镜像，通过一个属性将能够获得对方的值。<br>
	 * 它们遵循下述规则：
	 * <ul>
	 *     <li>互为镜像的两个属性，必须同时通过指定模式为{@code MIRROR_FOR}的{@link Link}注解指定对方；</li>
	 *     <li>互为镜像的两个属性，类型必须一致；</li>
	 *     <li>互为镜像的两个属性在获取值，且两者的值皆不同时，必须且仅允许有一个非默认值，该值被优先返回；</li>
	 *     <li>互为镜像的两个属性，在值都为默认值或都不为默认值时，两者的值必须相等；</li>
	 * </ul>
	 */
	MIRROR_FOR,

	/**
	 * <p>表示“原始属性”将作为“关联属性”的别名。
	 * <ul>
	 *     <li>当“原始属性”为默认值时，获取“关联属性”将返回“关联属性”本身的值；</li>
	 *     <li>当“原始属性”不为默认值时，获取“关联属性”将返回“原始属性”的值；</li>
	 * </ul>
	 */
	ALIAS_FOR,

	/**
	 * <p>表示“原始属性”将强制作为“关联属性”的别名。效果等同于在“原始属性”上添加{@link Alias}注解，
	 * 任何情况下，获取“关联属性”的值都将直接返回“原始属性”的值
	 */
	FORCE_ALIAS_FOR;

}
