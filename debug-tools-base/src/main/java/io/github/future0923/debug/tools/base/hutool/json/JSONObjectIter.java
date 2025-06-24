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
package io.github.future0923.debug.tools.base.hutool.json;

import java.util.Iterator;

/**
 * 此类用于在JSONAray中便于遍历JSONObject而封装的Iterable，可以借助foreach语法遍历
 * 
 * @author looly
 * @since 4.0.12
 */
public class JSONObjectIter implements Iterable<JSONObject> {

	Iterator<Object> iterator;
	
	public JSONObjectIter(Iterator<Object> iterator) {
		this.iterator = iterator;
	}

	@Override
	public Iterator<JSONObject> iterator() {
		return new Iterator<JSONObject>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public JSONObject next() {
				return (JSONObject) iterator.next();
			}

			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}

}
