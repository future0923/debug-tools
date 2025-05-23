/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.base.hutool.core.net;

import io.github.future0923.debug.tools.base.hutool.core.codec.PercentCodec;

/**
 * <a href="https://www.ietf.org/rfc/rfc3986.html">RFC3986</a> 编码实现<br>
 * 定义见：<a href="https://www.ietf.org/rfc/rfc3986.html#appendix-A">https://www.ietf.org/rfc/rfc3986.html#appendix-A</a>
 *
 * @author looly
 * @since 5.7.16
 */
public class RFC3986 {

	/**
	 * gen-delims = ":" / "/" / "?" / "#" / "[" / "]" / "@"
	 */
	public static final PercentCodec GEN_DELIMS = PercentCodec.of(":/?#[]@");

	/**
	 * sub-delims = "!" / "$" / "{@code &}" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
	 */
	public static final PercentCodec SUB_DELIMS = PercentCodec.of("!$&'()*+,;=");

	/**
	 * reserved = gen-delims / sub-delims<br>
	 * see：<a href="https://www.ietf.org/rfc/rfc3986.html#section-2.2">https://www.ietf.org/rfc/rfc3986.html#section-2.2</a>
	 */
	public static final PercentCodec RESERVED = GEN_DELIMS.orNew(SUB_DELIMS);

	/**
	 * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"<br>
	 * see: <a href="https://www.ietf.org/rfc/rfc3986.html#section-2.3">https://www.ietf.org/rfc/rfc3986.html#section-2.3</a>
	 */
	public static final PercentCodec UNRESERVED = PercentCodec.of(unreservedChars());

	/**
	 * pchar = unreserved / pct-encoded / sub-delims / ":" / "@"
	 */
	public static final PercentCodec PCHAR = UNRESERVED.orNew(SUB_DELIMS).or(PercentCodec.of(":@"));

	/**
	 * segment  = pchar<br>
	 * see: <a href="https://www.ietf.org/rfc/rfc3986.html#section-3.3">https://www.ietf.org/rfc/rfc3986.html#section-3.3</a>
	 */
	public static final PercentCodec SEGMENT = PCHAR;
	/**
	 * segment-nz-nc  = SEGMENT ; non-zero-length segment without any colon ":"
	 */
	public static final PercentCodec SEGMENT_NZ_NC = PercentCodec.of(SEGMENT).removeSafe(':');

	/**
	 * path = segment / "/"
	 */
	public static final PercentCodec PATH = SEGMENT.orNew(PercentCodec.of("/"));

	/**
	 * query = pchar / "/" / "?"
	 */
	public static final PercentCodec QUERY = PCHAR.orNew(PercentCodec.of("/?"));

	/**
	 * fragment     = pchar / "/" / "?"
	 */
	public static final PercentCodec FRAGMENT = QUERY;

	/**
	 * query中的value<br>
	 * value不能包含"{@code &}"，可以包含 "="
	 */
	public static final PercentCodec QUERY_PARAM_VALUE = PercentCodec.of(QUERY).removeSafe('&');

	/**
	 * query中的value编码器，严格模式，value中不能包含任何分隔符。
	 *
	 * @since 6.0.0
	 */
	public static final PercentCodec QUERY_PARAM_VALUE_STRICT = UNRESERVED;

	/**
	 * query中的key<br>
	 * key不能包含"{@code &}" 和 "="
	 */
	public static final PercentCodec QUERY_PARAM_NAME = PercentCodec.of(QUERY_PARAM_VALUE).removeSafe('=');

	/**
	 * query中的key编码器，严格模式，key中不能包含任何分隔符。
	 *
	 * @since 6.0.0
	 */
	public static final PercentCodec QUERY_PARAM_NAME_STRICT = UNRESERVED;

	/**
	 * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
	 *
	 * @return unreserved字符
	 */
	private static StringBuilder unreservedChars() {
		StringBuilder sb = new StringBuilder();

		// ALPHA
		for (char c = 'A'; c <= 'Z'; c++) {
			sb.append(c);
		}
		for (char c = 'a'; c <= 'z'; c++) {
			sb.append(c);
		}

		// DIGIT
		for (char c = '0'; c <= '9'; c++) {
			sb.append(c);
		}

		// "-" / "." / "_" / "~"
		sb.append("_.-~");

		return sb;
	}
}
