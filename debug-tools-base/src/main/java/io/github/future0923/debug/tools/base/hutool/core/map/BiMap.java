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


import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 双向Map<br>
 * 互换键值对不检查值是否有重复，如果有则后加入的元素替换先加入的元素<br>
 * 值的顺序在HashMap中不确定，所以谁覆盖谁也不确定，在有序的Map中按照先后顺序覆盖，保留最后的值<br>
 * 它与TableMap的区别是，BiMap维护两个Map实现高效的正向和反向查找
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @since 5.2.6
 */
public class BiMap<K, V> extends MapWrapper<K, V> {
	private static final long serialVersionUID = 1L;

	private Map<V, K> inverse;

	/**
	 * 构造
	 *
	 * @param raw 被包装的Map
	 */
	public BiMap(Map<K, V> raw) {
		super(raw);
	}

	@Override
	public V put(K key, V value) {
		final V oldValue = super.put(key, value);
		if (null != this.inverse) {
			if(null != oldValue){
				// issue#I88R5M
				// 如果put的key相同，value不同，需要在inverse中移除旧的关联
				this.inverse.remove(oldValue);
			}
			this.inverse.put(value, key);
		}
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		super.putAll(m);
		if (null != this.inverse) {
			m.forEach((key, value) -> this.inverse.put(value, key));
		}
	}

	@Override
	public V remove(Object key) {
		final V v = super.remove(key);
		if (null != this.inverse && null != v) {
			this.inverse.remove(v);
		}
		return v;
	}

	@Override
	public boolean remove(Object key, Object value) {
		return super.remove(key, value) && null != this.inverse && this.inverse.remove(value, key);
	}

	@Override
	public void clear() {
		super.clear();
		this.inverse = null;
	}

	/**
	 * 获取反向Map
	 *
	 * @return 反向Map
	 */
	public Map<V, K> getInverse() {
		if (null == this.inverse) {
			inverse = MapUtil.inverse(getRaw());
		}
		return this.inverse;
	}

	/**
	 * 根据值获得键
	 *
	 * @param value 值
	 * @return 键
	 */
	public K getKey(V value) {
		return getInverse().get(value);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		if (null != this.inverse) {
			this.inverse.putIfAbsent(value, key);
		}
		return super.putIfAbsent(key, value);
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		final V result = super.computeIfAbsent(key, mappingFunction);
		resetInverseMap();
		return result;
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		final V result = super.computeIfPresent(key, remappingFunction);
		resetInverseMap();
		return result;
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		final V result = super.compute(key, remappingFunction);
		resetInverseMap();
		return result;
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		final V result = super.merge(key, value, remappingFunction);
		resetInverseMap();
		return result;
	}

	/**
	 * 重置反转的Map，如果反转map为空，则不操作。
	 */
	private void resetInverseMap() {
		if (null != this.inverse) {
			inverse = null;
		}
	}
}
