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

import java.util.AbstractList;
import java.util.List;

/**
 * 列表分区或分段<br>
 * 通过传入分区长度，将指定列表分区为不同的块，每块区域的长度相同（最后一块可能小于长度）<br>
 * 分区是在原List的基础上进行的，返回的分区是不可变的抽象列表，原列表元素变更，分区中元素也会变更。
 * 参考：Guava的Lists#Partition
 *
 * @param <T> 元素类型
 * @author looly, guava
 * @since 5.7.10
 */
public class Partition<T> extends AbstractList<List<T>> {

	protected final List<T> list;
	protected final int size;

	/**
	 * 列表分区
	 *
	 * @param list 被分区的列表，非空
	 * @param size 每个分区的长度，必须&gt;0
	 */
	public Partition(List<T> list, int size) {
		this.list = Assert.notNull(list);
		this.size = Math.min(list.size(), size);
	}

	@Override
	public List<T> get(int index) {
		final int start = index * size;
		final int end = Math.min(start + size, list.size());
		return list.subList(start, end);
	}

	@Override
	public int size() {
		// 此处采用动态计算，以应对list变
		final int size = this.size;
		if(0 == size){
			return 0;
		}

		final int total = list.size();
		// 类似于判断余数，当总数非整份size时，多余的数>=1，则相当于被除数多一个size，做到+1目的
		// 类似于：if(total % size > 0){length += 1;}
		return (total + size - 1) / size;
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}
}
