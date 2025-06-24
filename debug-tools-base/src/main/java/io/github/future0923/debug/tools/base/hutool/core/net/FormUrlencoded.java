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
package io.github.future0923.debug.tools.base.hutool.core.net;

import io.github.future0923.debug.tools.base.hutool.core.codec.PercentCodec;

/**
 * application/x-www-form-urlencoded，遵循W3C HTML Form content types规范，如空格须转+，+须被编码<br>
 * 规范见：https://url.spec.whatwg.org/#urlencoded-serializing
 *
 * @since 5.7.16
 */
public class FormUrlencoded {

	/**
	 * query中的value，默认除"-", "_", ".", "*"外都编码<br>
	 * 这个类似于JDK提供的{@link java.net.URLEncoder}
	 */
	public static final PercentCodec ALL = PercentCodec.of(RFC3986.UNRESERVED)
			.removeSafe('~').addSafe('*').setEncodeSpaceAsPlus(true);
}
