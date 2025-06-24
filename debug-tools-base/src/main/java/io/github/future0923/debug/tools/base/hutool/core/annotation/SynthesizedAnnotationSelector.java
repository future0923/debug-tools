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


/**
 * 注解选择器，指定两个注解，选择其中一个返回。<br>
 * 该接口用于在{@link SynthesizedAggregateAnnotation}中用于从一批相同的注解对象中筛选最终用于合成注解对象。
 *
 * @author huangchengxing
 */
@FunctionalInterface
public interface SynthesizedAnnotationSelector {

	/**
	 * 返回距离根对象更近的注解，当距离一样时优先返回旧注解
	 */
	SynthesizedAnnotationSelector NEAREST_AND_OLDEST_PRIORITY = new NearestAndOldestPrioritySelector();

	/**
	 * 返回距离根对象更近的注解，当距离一样时优先返回新注解
	 */
	SynthesizedAnnotationSelector NEAREST_AND_NEWEST_PRIORITY = new NearestAndNewestPrioritySelector();

	/**
	 * 返回距离根对象更远的注解，当距离一样时优先返回旧注解
	 */
	SynthesizedAnnotationSelector FARTHEST_AND_OLDEST_PRIORITY = new FarthestAndOldestPrioritySelector();

	/**
	 * 返回距离根对象更远的注解，当距离一样时优先返回新注解
	 */
	SynthesizedAnnotationSelector FARTHEST_AND_NEWEST_PRIORITY = new FarthestAndNewestPrioritySelector();

	/**
	 * 比较两个被合成的注解，选择其中的一个并返回
	 *
	 * @param <T>           复合注解类型
	 * @param oldAnnotation 已存在的注解，该参数不允许为空
	 * @param newAnnotation 新获取的注解，该参数不允许为空
	 * @return 被合成的注解
	 */
	<T extends SynthesizedAnnotation> T choose(T oldAnnotation, T newAnnotation);

	/**
	 * 返回距离根对象更近的注解，当距离一样时优先返回旧注解
	 */
	class NearestAndOldestPrioritySelector implements SynthesizedAnnotationSelector {
		@Override
		public <T extends SynthesizedAnnotation> T choose(T oldAnnotation, T newAnnotation) {
			return Hierarchical.Selector.NEAREST_AND_OLDEST_PRIORITY.choose(oldAnnotation, newAnnotation);
		}
	}

	/**
	 * 返回距离根对象更近的注解，当距离一样时优先返回新注解
	 */
	class NearestAndNewestPrioritySelector implements SynthesizedAnnotationSelector {
		@Override
		public <T extends SynthesizedAnnotation> T choose(T oldAnnotation, T newAnnotation) {
			return Hierarchical.Selector.NEAREST_AND_NEWEST_PRIORITY.choose(oldAnnotation, newAnnotation);
		}
	}

	/**
	 * 返回距离根对象更远的注解，当距离一样时优先返回旧注解
	 */
	class FarthestAndOldestPrioritySelector implements SynthesizedAnnotationSelector {
		@Override
		public <T extends SynthesizedAnnotation> T choose(T oldAnnotation, T newAnnotation) {
			return Hierarchical.Selector.FARTHEST_AND_OLDEST_PRIORITY.choose(oldAnnotation, newAnnotation);
		}
	}

	/**
	 * 返回距离根对象更远的注解，当距离一样时优先返回新注解
	 */
	class FarthestAndNewestPrioritySelector implements SynthesizedAnnotationSelector {
		@Override
		public <T extends SynthesizedAnnotation> T choose(T oldAnnotation, T newAnnotation) {
			return Hierarchical.Selector.FARTHEST_AND_NEWEST_PRIORITY.choose(oldAnnotation, newAnnotation);
		}
	}

}
