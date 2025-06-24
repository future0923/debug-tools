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

import java.util.List;
import java.util.RandomAccess;

/**
 * 列表分区或分段（可随机访问列表）<br>
 * 通过传入分区长度，将指定列表分区为不同的块，每块区域的长度相同（最后一块可能小于长度）<br>
 * 分区是在原List的基础上进行的，返回的分区是不可变的抽象列表，原列表元素变更，分区中元素也会变更。
 * 参考：Guava的Lists#RandomAccessPartition
 *
 * @param <T> 元素类型
 * @author looly, guava
 * @since 5.7.10
 */
public class RandomAccessPartition<T> extends Partition<T> implements RandomAccess {

	/**
	 * 构造
	 *
	 * @param list 被分区的列表，必须实现{@link RandomAccess}
	 * @param size 每个分区的长度
	 */
	public RandomAccessPartition(List<T> list, int size) {
		super(list, size);
	}
}
