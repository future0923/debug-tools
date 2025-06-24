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
package io.github.future0923.debug.tools.base.hutool.json.serialize;

import io.github.future0923.debug.tools.base.hutool.json.JSON;

/**
 * 序列化接口，通过实现此接口，实现自定义的对象转换为JSON的操作
 *
 * @param <T> JSON类型，可以是JSONObject或者JSONArray
 * @param <V> 对象类型
 * @author Looly
 */
@FunctionalInterface
public interface JSONSerializer<T extends JSON, V> {

	/**
	 * 序列化实现，通过实现此方法，将指定类型的对象转换为{@link JSON}对象<br>
	 * 转换后的对象可以为JSONObject也可以为JSONArray，首先new一个空的JSON，然后将需要的数据字段put到JSON对象中去即可。
	 *
	 * @param json JSON，可以为JSONObject或者JSONArray
	 * @param bean 指定类型对象
	 */
	void serialize(T json, V bean);
}
