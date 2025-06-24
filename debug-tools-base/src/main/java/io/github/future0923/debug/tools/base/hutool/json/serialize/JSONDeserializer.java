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
 * JSON反序列话自定义实现类
 * 
 * @author Looly
 *
 * @param <T> 反序列化后的类型
 */
@FunctionalInterface
public interface JSONDeserializer<T> {
	
	/**
	 * 反序列化，通过实现此方法，自定义实现JSON转换为指定类型的逻辑
	 * 
	 * @param json {@link JSON}
	 * @return 目标对象
	 */
	T deserialize(JSON json);
}
