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
package io.github.future0923.debug.tools.base.hutool.core.collection;

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 包装 {@link NodeList} 的{@link Iterator}
 * <p>
 * 此 iterator 不支持 {@link #remove()} 方法。
 *
 * @author apache commons,looly
 * @see NodeList
 * @since 5.8.0
 */
public class NodeListIter implements ResettableIter<Node> {

	private final NodeList nodeList;
	/**
	 * 当前位置索引
	 */
	private int index = 0;

	/**
	 * 构造, 根据给定{@link NodeList} 创建{@code NodeListIterator}
	 *
	 * @param nodeList {@link NodeList}，非空
	 */
	public NodeListIter(final NodeList nodeList) {
		this.nodeList = Assert.notNull(nodeList, "NodeList must not be null.");
	}

	@Override
	public boolean hasNext() {
		return nodeList != null && index < nodeList.getLength();
	}

	@Override
	public Node next() {
		if (nodeList != null && index < nodeList.getLength()) {
			return nodeList.item(index++);
		}
		throw new NoSuchElementException("underlying nodeList has no more elements");
	}

	/**
	 * Throws {@link UnsupportedOperationException}.
	 *
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove() method not supported for a NodeListIterator.");
	}

	@Override
	public void reset() {
		this.index = 0;
	}
}
