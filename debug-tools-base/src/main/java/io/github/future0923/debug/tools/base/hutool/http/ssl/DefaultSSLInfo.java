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
package io.github.future0923.debug.tools.base.hutool.http.ssl;

import io.github.future0923.debug.tools.base.hutool.core.util.JdkUtil;

import javax.net.ssl.SSLSocketFactory;

/**
 * 默认的全局SSL配置，当用户未设置相关信息时，使用默认设置，默认设置为单例模式。
 *
 * @author looly
 * @since 5.1.2
 */
public class DefaultSSLInfo {
	/**
	 * 默认信任全部的域名校验器
	 */
	public static final TrustAnyHostnameVerifier TRUST_ANY_HOSTNAME_VERIFIER;
	/**
	 * 默认的SSLSocketFactory，区分安卓
	 */
	public static final SSLSocketFactory DEFAULT_SSF;

	static {
		TRUST_ANY_HOSTNAME_VERIFIER = new TrustAnyHostnameVerifier();
		if (JdkUtil.IS_ANDROID) {
			// 兼容android低版本SSL连接
			DEFAULT_SSF = new AndroidSupportSSLFactory();
		} else {
			DEFAULT_SSF = new DefaultSSLFactory();
		}
	}
}
