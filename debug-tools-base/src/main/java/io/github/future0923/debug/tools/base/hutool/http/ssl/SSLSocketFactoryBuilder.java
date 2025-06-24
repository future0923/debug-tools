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

import io.github.future0923.debug.tools.base.hutool.core.net.SSLContextBuilder;
import io.github.future0923.debug.tools.base.hutool.core.net.SSLProtocols;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * SSLSocketFactory构建器
 *
 * @author Looly
 * @see SSLContextBuilder
 * @deprecated 请使用 {@link SSLContextBuilder}
 */
@Deprecated
public class SSLSocketFactoryBuilder implements SSLProtocols {

	SSLContextBuilder sslContextBuilder;

	/**
	 * 构造
	 */
	public SSLSocketFactoryBuilder() {
		this.sslContextBuilder = SSLContextBuilder.create();
	}

	/**
	 * 创建 SSLSocketFactoryBuilder
	 *
	 * @return SSLSocketFactoryBuilder
	 */
	public static SSLSocketFactoryBuilder create() {
		return new SSLSocketFactoryBuilder();
	}

	/**
	 * 设置协议
	 *
	 * @param protocol 协议
	 * @return 自身
	 */
	public SSLSocketFactoryBuilder setProtocol(String protocol) {
		this.sslContextBuilder.setProtocol(protocol);
		return this;
	}

	/**
	 * 设置信任信息
	 *
	 * @param trustManagers TrustManager列表
	 * @return 自身
	 */
	public SSLSocketFactoryBuilder setTrustManagers(TrustManager... trustManagers) {
		this.sslContextBuilder.setTrustManagers(trustManagers);
		return this;
	}

	/**
	 * 设置 JSSE key managers
	 *
	 * @param keyManagers JSSE key managers
	 * @return 自身
	 */
	public SSLSocketFactoryBuilder setKeyManagers(KeyManager... keyManagers) {
		this.sslContextBuilder.setKeyManagers(keyManagers);
		return this;
	}

	/**
	 * 设置 SecureRandom
	 *
	 * @param secureRandom SecureRandom
	 * @return 自己
	 */
	public SSLSocketFactoryBuilder setSecureRandom(SecureRandom secureRandom) {
		this.sslContextBuilder.setSecureRandom(secureRandom);
		return this;
	}

	/**
	 * 构建SSLSocketFactory
	 *
	 * @return SSLSocketFactory
	 * @throws NoSuchAlgorithmException 无此算法
	 * @throws KeyManagementException   Key管理异常
	 */
	public SSLSocketFactory build() throws NoSuchAlgorithmException, KeyManagementException {
		return this.sslContextBuilder.buildChecked().getSocketFactory();
	}
}
