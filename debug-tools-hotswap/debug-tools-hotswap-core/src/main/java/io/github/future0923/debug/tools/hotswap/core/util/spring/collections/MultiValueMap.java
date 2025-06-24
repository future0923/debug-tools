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
package io.github.future0923.debug.tools.hotswap.core.util.spring.collections;

import java.util.List;
import java.util.Map;

/**
 * Extension of the {@code Map} interface that stores multiple values.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {

    /**
     * Return the first value for the given key.
     * 
     * @param key
     *            the key
     * @return the first value for the specified key, or {@code null}
     */
    V getFirst(K key);

    /**
     * Add the given single value to the current list of values for the given
     * key.
     * 
     * @param key
     *            the key
     * @param value
     *            the value to be added
     */
    void add(K key, V value);

    /**
     * Set the given single value under the given key.
     * 
     * @param key
     *            the key
     * @param value
     *            the value to set
     */
    void set(K key, V value);

    /**
     * Set the given values under.
     * 
     * @param values
     *            the values.
     */
    void setAll(Map<K, V> values);

    /**
     * Returns the first values contained in this {@code MultiValueMap}.
     * 
     * @return a single value representation of this map
     */
    Map<K, V> toSingleValueMap();

}