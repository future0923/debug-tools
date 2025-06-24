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

import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * SSL(Secure Sockets Layer 安全套接字协议)相关工具封装
 *
 * @author looly
 * @since 5.5.2
 */
public class SSLUtil {

	/**
	 * 创建{@link SSLContext}，默认新人全部
	 *
	 * @param protocol     SSL协议，例如TLS等
	 * @return {@link SSLContext}
	 * @throws IORuntimeException 包装 GeneralSecurityException异常
	 * @since 5.7.8
	 */
	public static SSLContext createSSLContext(String protocol) throws IORuntimeException{
		return SSLContextBuilder.create().setProtocol(protocol).build();
	}

	/**
	 * 创建{@link SSLContext}
	 *
	 * @param protocol     SSL协议，例如TLS等
	 * @param keyManager   密钥管理器,{@code null}表示无
	 * @param trustManager 信任管理器, {@code null}表示无
	 * @return {@link SSLContext}
	 * @throws IORuntimeException 包装 GeneralSecurityException异常
	 */
	public static SSLContext createSSLContext(String protocol, KeyManager keyManager, TrustManager trustManager)
			throws IORuntimeException {
		return createSSLContext(protocol,
				keyManager == null ? null : new KeyManager[]{keyManager},
				trustManager == null ? null : new TrustManager[]{trustManager});
	}

	/**
	 * 创建和初始化{@link SSLContext}
	 *
	 * @param protocol      SSL协议，例如TLS等
	 * @param keyManagers   密钥管理器,{@code null}表示无
	 * @param trustManagers 信任管理器, {@code null}表示无
	 * @return {@link SSLContext}
	 * @throws IORuntimeException 包装 GeneralSecurityException异常
	 */
	public static SSLContext createSSLContext(String protocol, KeyManager[] keyManagers, TrustManager[] trustManagers) throws IORuntimeException {
		return SSLContextBuilder.create()
				.setProtocol(protocol)
				.setKeyManagers(keyManagers)
				.setTrustManagers(trustManagers).build();
	}
}
