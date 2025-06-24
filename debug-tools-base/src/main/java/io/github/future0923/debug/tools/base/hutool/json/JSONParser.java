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

import io.github.future0923.debug.tools.base.hutool.core.lang.Filter;
import io.github.future0923.debug.tools.base.hutool.core.lang.mutable.Mutable;
import io.github.future0923.debug.tools.base.hutool.core.lang.mutable.MutablePair;

/**
 * JSON字符串解析器
 *
 * @author looly
 * @since 5.8.0
 */
public class JSONParser {

	/**
	 * 创建JSONParser
	 *
	 * @param tokener {@link JSONTokener}
	 * @return JSONParser
	 */
	public static JSONParser of(JSONTokener tokener) {
		return new JSONParser(tokener);
	}

	private final JSONTokener tokener;

	/**
	 * 构造
	 *
	 * @param tokener {@link JSONTokener}
	 */
	public JSONParser(JSONTokener tokener) {
		this.tokener = tokener;
	}

	// region parseTo

	/**
	 * 解析{@link JSONTokener}中的字符到目标的{@link JSONObject}中
	 *
	 * @param jsonObject {@link JSONObject}
	 * @param filter     键值对过滤编辑器，可以通过实现此接口，完成解析前对键值对的过滤和修改操作，{@code null}表示不过滤
	 */
	public void parseTo(JSONObject jsonObject, Filter<MutablePair<String, Object>> filter) {
		final JSONTokener tokener = this.tokener;

		if (tokener.nextClean() != '{') {
			throw tokener.syntaxError("A JSONObject text must begin with '{'");
		}

		char prev;
		char c;
		String key;
		while (true) {
			prev = tokener.getPrevious();
			c = tokener.nextClean();
			switch (c) {
				case 0:
					throw tokener.syntaxError("A JSONObject text must end with '}'");
				case '}':
					return;
				case '{':
				case '[':
					if (prev == '{') {
						throw tokener.syntaxError("A JSONObject can not directly nest another JSONObject or JSONArray.");
					}
				default:
					tokener.back();
					key = tokener.nextStringValue();
			}

			// The key is followed by ':'.

			c = tokener.nextClean();
			if (c != ':') {
				throw tokener.syntaxError("Expected a ':' after a key");
			}

			jsonObject.set(key, tokener.nextValue(), filter, jsonObject.getConfig().isCheckDuplicate());

			// Pairs are separated by ','.

			switch (tokener.nextClean()) {
				case ';':
				case ',':
					if (tokener.nextClean() == '}') {
						// issue#2380
						// 尾后逗号（Trailing Commas），JSON中虽然不支持，但是ECMAScript 2017支持，此处做兼容。
						return;
					}
					tokener.back();
					break;
				case '}':
					return;
				default:
					throw tokener.syntaxError("Expected a ',' or '}'");
			}
		}
	}

	/**
	 * 解析JSON字符串到{@link JSONArray}中
	 *
	 * @param jsonArray {@link JSONArray}
	 * @param filter    键值对过滤编辑器，可以通过实现此接口，完成解析前对值的过滤和修改操作，{@code null} 表示不过滤
	 */
	public void parseTo(JSONArray jsonArray, Filter<Mutable<Object>> filter) {
		final JSONTokener x = this.tokener;

		if (x.nextClean() != '[') {
			throw x.syntaxError("A JSONArray text must start with '['");
		}
		if (x.nextClean() != ']') {
			x.back();
			for (; ; ) {
				if (x.nextClean() == ',') {
					x.back();
					jsonArray.addRaw(JSONNull.NULL, filter);
				} else {
					x.back();
					jsonArray.addRaw(x.nextValue(), filter);
				}
				switch (x.nextClean()) {
					case ',':
						if (x.nextClean() == ']') {
							return;
						}
						x.back();
						break;
					case ']':
						return;
					default:
						throw x.syntaxError("Expected a ',' or ']'");
				}
			}
		}
	}
	// endregion
}
