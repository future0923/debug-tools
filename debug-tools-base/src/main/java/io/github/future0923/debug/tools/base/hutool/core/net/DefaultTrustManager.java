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

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.X509Certificate;

/**
 * 默认信任管理器，默认信任所有客户端和服务端证书<br>
 * 继承{@link X509ExtendedTrustManager}的原因见：https://blog.csdn.net/ghaohao/article/details/79454913
 *
 * @author Looly
 * @since 5.5.7
 */
public class DefaultTrustManager extends X509ExtendedTrustManager {

	/**
	 * 默认的全局单例默认信任管理器，默认信任所有客户端和服务端证书
	 * @since 5.7.8
	 */
	public static DefaultTrustManager INSTANCE = new DefaultTrustManager();

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) {
	}

	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
	}

	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
	}
}
