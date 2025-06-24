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



import java.util.Map;
import java.util.Objects;

/**
 * 树节点 每个属性都可以在{@link TreeNodeConfig}中被重命名<br>
 * 在你的项目里它可以是部门实体、地区实体等任意类树节点实体
 * 类树节点实体: 包含key，父Key.不限于这些属性的可以构造成一颗树的实体对象
 *
 * @param <T> ID类型
 * @author liangbaikai
 */
public class TreeNode<T> implements Node<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	private T id;

	/**
	 * 父节点ID
	 */
	private T parentId;

	/**
	 * 名称
	 */
	private CharSequence name;

	/**
	 * 顺序 越小优先级越高 默认0
	 */
	private Comparable<?> weight = 0;

	/**
	 * 扩展字段
	 */
	private Map<String, Object> extra;


	/**
	 * 空构造
	 */
	public TreeNode() {
	}

	/**
	 * 构造
	 *
	 * @param id       ID
	 * @param parentId 父节点ID
	 * @param name     名称
	 * @param weight   权重
	 */
	public TreeNode(T id, T parentId, String name, Comparable<?> weight) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		if (weight != null) {
			this.weight = weight;
		}

	}

	@Override
	public T getId() {
		return id;
	}

	@Override
	public TreeNode<T> setId(T id) {
		this.id = id;
		return this;
	}

	@Override
	public T getParentId() {
		return this.parentId;
	}

	@Override
	public TreeNode<T> setParentId(T parentId) {
		this.parentId = parentId;
		return this;
	}

	@Override
	public CharSequence getName() {
		return name;
	}

	@Override
	public TreeNode<T> setName(CharSequence name) {
		this.name = name;
		return this;
	}

	@Override
	public Comparable<?> getWeight() {
		return weight;
	}

	@Override
	public TreeNode<T> setWeight(Comparable<?> weight) {
		this.weight = weight;
		return this;
	}

	/**
	 * 获取扩展字段
	 *
	 * @return 扩展字段Map
	 * @since 5.2.5
	 */
	public Map<String, Object> getExtra() {
		return extra;
	}

	/**
	 * 设置扩展字段
	 *
	 * @param extra 扩展字段
	 * @return this
	 * @since 5.2.5
	 */
	public TreeNode<T> setExtra(Map<String, Object> extra) {
		this.extra = extra;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TreeNode<?> treeNode = (TreeNode<?>) o;
		return Objects.equals(id, treeNode.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
