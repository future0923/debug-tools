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
package io.github.future0923.debug.tools.base.hutool.core.lang.tree;

import io.github.future0923.debug.tools.base.hutool.core.comparator.CompareUtil;

import java.io.Serializable;

/**
 * 节点接口，提供节点相关的的方法定义
 *
 * @param <T> ID类型
 * @author looly
 * @since 5.2.4
 */
public interface Node<T> extends Comparable<Node<T>>, Serializable {

	/**
	 * 获取ID
	 *
	 * @return ID
	 */
	T getId();

	/**
	 * 设置ID
	 *
	 * @param id ID
	 * @return this
	 */
	Node<T> setId(T id);

	/**
	 * 获取父节点ID
	 *
	 * @return 父节点ID
	 */
	T getParentId();

	/**
	 * 设置父节点ID
	 *
	 * @param parentId 父节点ID
	 * @return this
	 */
	Node<T> setParentId(T parentId);

	/**
	 * 获取节点标签名称
	 *
	 * @return 节点标签名称
	 */
	CharSequence getName();

	/**
	 * 设置节点标签名称
	 *
	 * @param name 节点标签名称
	 * @return this
	 */
	Node<T> setName(CharSequence name);

	/**
	 * 获取权重
	 *
	 * @return 权重
	 */
	Comparable<?> getWeight();

	/**
	 * 设置权重
	 *
	 * @param weight 权重
	 * @return this
	 */
	Node<T> setWeight(Comparable<?> weight);

	@SuppressWarnings({"unchecked", "rawtypes", "NullableProblems"})
	@Override
	default int compareTo(Node node) {
		if(null == node){
			return 1;
		}
		final Comparable weight = this.getWeight();
		final Comparable weightOther = node.getWeight();
		return CompareUtil.compare(weight, weightOther);
	}
}
