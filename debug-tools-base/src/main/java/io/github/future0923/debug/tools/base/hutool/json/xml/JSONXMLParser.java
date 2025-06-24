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
package io.github.future0923.debug.tools.base.hutool.json.xml;

import io.github.future0923.debug.tools.base.hutool.json.InternalJSONUtil;
import io.github.future0923.debug.tools.base.hutool.json.JSONException;
import io.github.future0923.debug.tools.base.hutool.json.JSONObject;
import io.github.future0923.debug.tools.base.hutool.json.XML;
import io.github.future0923.debug.tools.base.hutool.json.XMLTokener;

/**
 * XML解析器，将XML解析为JSON对象
 *
 * @author JSON.org, looly
 * @since 5.7.11
 */
public class JSONXMLParser {

	/**
	 * 转换XML为JSONObject
	 * 转换过程中一些信息可能会丢失，JSON中无法区分节点和属性，相同的节点将被处理为JSONArray。
	 *
	 * @param jo          JSONObject
	 * @param xmlStr      XML字符串
	 * @param keepStrings 如果为{@code true}，则值保持String类型，不转换为数字或boolean
	 * @throws JSONException 解析异常
	 */
	public static void parseJSONObject(JSONObject jo, String xmlStr, boolean keepStrings) throws JSONException {
		parseJSONObject(jo, xmlStr, ParseConfig.of().setKeepStrings(keepStrings));
	}

	/**
	 * 转换XML为JSONObject
	 * 转换过程中一些信息可能会丢失，JSON中无法区分节点和属性，相同的节点将被处理为JSONArray。
	 *
	 * @param xmlStr      XML字符串
	 * @param jo          JSONObject
	 * @param parseConfig 解析选项
	 * @throws JSONException 解析异常
	 */
	public static void parseJSONObject(final JSONObject jo, final String xmlStr, final ParseConfig parseConfig) throws JSONException {
		final XMLTokener x = new XMLTokener(xmlStr, jo.getConfig());
		while (x.more() && x.skipPast("<")) {
			parse(x, jo, null, parseConfig, 0);
		}
	}

	/**
	 * Scan the content following the named tag, attaching it to the context.
	 *
	 * @param x       The XMLTokener containing the source string.
	 * @param context The JSONObject that will include the new material.
	 * @param name    The tag name.
	 * @param parseConfig 解析选项
	 * @param currentNestingDepth 当前层级
	 * @return true if the close tag is processed.
	 * @throws JSONException JSON异常
	 */
	private static boolean parse(XMLTokener x, JSONObject context, String name, ParseConfig parseConfig, int currentNestingDepth) throws JSONException {
		char c;
		int i;
		JSONObject jsonobject;
		String string;
		String tagName;
		Object token;

		token = x.nextToken();

		if (token == XML.BANG) {
			c = x.next();
			if (c == '-') {
				if (x.next() == '-') {
					x.skipPast("-->");
					return false;
				}
				x.back();
			} else if (c == '[') {
				token = x.nextToken();
				if ("CDATA".equals(token)) {
					if (x.next() == '[') {
						string = x.nextCDATA();
						if (string.length() > 0) {
							context.accumulate("content", string);
						}
						return false;
					}
				}
				throw x.syntaxError("Expected 'CDATA['");
			}
			i = 1;
			do {
				token = x.nextMeta();
				if (token == null) {
					throw x.syntaxError("Missing '>' after '<!'.");
				} else if (token == XML.LT) {
					i += 1;
				} else if (token == XML.GT) {
					i -= 1;
				}
			} while (i > 0);
			return false;
		} else if (token == XML.QUEST) {

			// <?
			x.skipPast("?>");
			return false;
		} else if (token == XML.SLASH) {

			// Close tag </

			token = x.nextToken();
			if (name == null) {
				throw x.syntaxError("Mismatched close tag " + token);
			}
			if (!token.equals(name)) {
				throw x.syntaxError("Mismatched " + name + " and " + token);
			}
			if (x.nextToken() != XML.GT) {
				throw x.syntaxError("Misshaped close tag");
			}
			return true;

		} else if (token instanceof Character) {
			throw x.syntaxError("Misshaped tag");

			// Open tag <

		} else {
			tagName = (String) token;
			token = null;
			jsonobject = new JSONObject();
			final boolean keepStrings = parseConfig.isKeepStrings();
			for (; ; ) {
				if (token == null) {
					token = x.nextToken();
				}

				// attribute = value
				if (token instanceof String) {
					string = (String) token;
					token = x.nextToken();
					if (token == XML.EQ) {
						token = x.nextToken();
						if (!(token instanceof String)) {
							throw x.syntaxError("Missing value");
						}
						jsonobject.accumulate(string, keepStrings ? token : InternalJSONUtil.stringToValue((String) token));
						token = null;
					} else {
						jsonobject.accumulate(string, "");
					}

				} else if (token == XML.SLASH) {
					// Empty tag <.../>
					if (x.nextToken() != XML.GT) {
						throw x.syntaxError("Misshaped tag");
					}
					if (jsonobject.size() > 0) {
						context.accumulate(tagName, jsonobject);
					} else {
						context.accumulate(tagName, "");
					}
					return false;

				} else if (token == XML.GT) {
					// Content, between <...> and </...>
					for (; ; ) {
						token = x.nextContent();
						if (token == null) {
							if (tagName != null) {
								throw x.syntaxError("Unclosed tag " + tagName);
							}
							return false;
						} else if (token instanceof String) {
							string = (String) token;
							if (!string.isEmpty()) {
								jsonobject.accumulate("content", keepStrings ? token : InternalJSONUtil.stringToValue(string));
							}

						} else if (token == XML.LT) {
							// Nested element
							// issue#2748 of CVE-2022-45688
							final int maxNestingDepth = parseConfig.getMaxNestingDepth();
							if (maxNestingDepth > -1 && currentNestingDepth >= maxNestingDepth) {
								throw x.syntaxError("Maximum nesting depth of " + maxNestingDepth + " reached");
							}

							// Nested element
							if (parse(x, jsonobject, tagName, parseConfig, currentNestingDepth + 1)) {
								if (jsonobject.isEmpty()) {
									context.accumulate(tagName, "");
								} else if (jsonobject.size() == 1 && jsonobject.get("content") != null) {
									context.accumulate(tagName, jsonobject.get("content"));
								} else {
									context.accumulate(tagName, jsonobject);
								}
								return false;
							}
						}
					}
				} else {
					throw x.syntaxError("Misshaped tag");
				}
			}
		}
	}
}
