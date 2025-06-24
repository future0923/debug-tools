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
package io.github.future0923.debug.tools.base.hutool.core.map;


import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 忽略大小写的{@link TreeMap}<br>
 * 对KEY忽略大小写，get("Value")和get("value")获得的值相同，put进入的值也会被覆盖
 *
 * @author Looly
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @since 3.3.1
 */
public class CaseInsensitiveTreeMap<K, V> extends CaseInsensitiveMap<K, V> {
	private static final long serialVersionUID = 4043263744224569870L;

	// ------------------------------------------------------------------------- Constructor start
	/**
	 * 构造
	 */
	public CaseInsensitiveTreeMap() {
		this((Comparator<? super K>) null);
	}

	/**
	 * 构造
	 *
	 * @param m Map
	 * @since 3.1.2
	 */
	public CaseInsensitiveTreeMap(Map<? extends K, ? extends V> m) {
		this();
		this.putAll(m);
	}

	/**
	 * 构造
	 *
	 * @param m Map，初始Map，键值对会被复制到新的TreeMap中
	 * @since 3.1.2
	 */
	public CaseInsensitiveTreeMap(SortedMap<? extends K, ? extends V> m) {
		super(MapBuilder.create(new TreeMap<K, V>(m)));
	}

	/**
	 * 构造
	 *
	 * @param comparator 比较器，{@code null}表示使用默认比较器
	 */
	public CaseInsensitiveTreeMap(Comparator<? super K> comparator) {
		super(MapBuilder.create(new TreeMap<>(comparator)));
	}
	// ------------------------------------------------------------------------- Constructor end
}
