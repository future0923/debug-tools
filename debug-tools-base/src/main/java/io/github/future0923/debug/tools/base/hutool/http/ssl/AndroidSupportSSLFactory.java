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

import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.net.SSLProtocols;

/**
 * 兼容android低版本SSL连接<br>
 * 在测试HttpUrlConnection的时候，发现一部分手机无法连接[GithubPage]
 *
 * <p>
 * 最后发现原来是某些SSL协议没有开启
 *
 * @author MikaGuraNTK
 */
public class AndroidSupportSSLFactory extends CustomProtocolsSSLFactory {

	// Android低版本不重置的话某些SSL访问就会失败
	private static final String[] protocols = {
			SSLProtocols.SSLv3, SSLProtocols.TLSv1, SSLProtocols.TLSv11, SSLProtocols.TLSv12};

	public AndroidSupportSSLFactory() throws IORuntimeException {
		super(protocols);
	}

}
