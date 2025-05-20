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
