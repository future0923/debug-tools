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

import io.github.future0923.debug.tools.base.hutool.core.bean.BeanUtil;

/**
 * JSON支持<br>
 * 继承此类实现实体类与JSON的相互转换
 *
 * @author Looly
 */
public class JSONSupport implements JSONString, JSONBeanParser<JSON> {

	/**
	 * JSON String转Bean
	 *
	 * @param jsonString JSON String
	 */
	public void parse(String jsonString) {
		parse(new JSONObject(jsonString));
	}

	/**
	 * JSON转Bean
	 *
	 * @param json JSON
	 */
	@Override
	public void parse(JSON json) {
		final JSONSupport support = JSONConverter.jsonToBean(getClass(), json, false);
		BeanUtil.copyProperties(support, this);
	}

	/**
	 * @return JSON对象
	 */
	public JSONObject toJSON() {
		return new JSONObject(this);
	}

	@Override
	public String toJSONString() {
		return toJSON().toString();
	}

	/**
	 * 美化的JSON（使用回车缩进显示JSON），用于打印输出debug
	 *
	 * @return 美化的JSON
	 */
	public String toPrettyString() {
		return toJSON().toStringPretty();
	}

	@Override
	public String toString() {
		return toJSONString();
	}
}
