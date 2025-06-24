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

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

/**
 * 代理Socket工厂，用于创建代理Socket<br>
 * 来自commons-net的DefaultSocketFactory
 *
 * @author commons-net, looly
 * @since 5.8.23
 */
public class ProxySocketFactory extends SocketFactory {

	/**
	 * 创建代理SocketFactory
	 * @param proxy 代理对象
	 * @return {@code ProxySocketFactory}
	 */
	public static ProxySocketFactory of(final Proxy proxy) {
		return new ProxySocketFactory(proxy);
	}

	private final Proxy proxy;

	/**
	 * 构造
	 *
	 * @param proxy Socket代理
	 */
	public ProxySocketFactory(final Proxy proxy) {
		this.proxy = proxy;
	}

	@Override
	public Socket createSocket() {
		if (proxy != null) {
			return new Socket(proxy);
		}
		return new Socket();
	}

	@Override
	public Socket createSocket(final InetAddress address, final int port) throws IOException {
		if (proxy != null) {
			final Socket s = new Socket(proxy);
			s.connect(new InetSocketAddress(address, port));
			return s;
		}
		return new Socket(address, port);
	}

	@Override
	public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddr, final int localPort) throws IOException {
		if (proxy != null) {
			final Socket s = new Socket(proxy);
			s.bind(new InetSocketAddress(localAddr, localPort));
			s.connect(new InetSocketAddress(address, port));
			return s;
		}
		return new Socket(address, port, localAddr, localPort);
	}

	@Override
	public Socket createSocket(final String host, final int port) throws IOException {
		if (proxy != null) {
			final Socket s = new Socket(proxy);
			s.connect(new InetSocketAddress(host, port));
			return s;
		}
		return new Socket(host, port);
	}

	@Override
	public Socket createSocket(final String host, final int port, final InetAddress localAddr, final int localPort) throws IOException {
		if (proxy != null) {
			final Socket s = new Socket(proxy);
			s.bind(new InetSocketAddress(localAddr, localPort));
			s.connect(new InetSocketAddress(host, port));
			return s;
		}
		return new Socket(host, port, localAddr, localPort);
	}
}
