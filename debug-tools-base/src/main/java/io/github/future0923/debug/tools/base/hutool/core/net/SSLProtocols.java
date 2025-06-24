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

/**
 * SSL或TLS协议
 *
 * @author looly
 * @since 5.7.8
 */
public interface SSLProtocols {

	/**
	 * Supports some version of SSL; may support other versions
	 */
	String SSL = "SSL";
	/**
	 * Supports SSL version 2 or later; may support other versions
	 */
	String SSLv2 = "SSLv2";
	/**
	 * Supports SSL version 3; may support other versions
	 */
	String SSLv3 = "SSLv3";

	/**
	 * Supports some version of TLS; may support other versions
	 */
	String TLS = "TLS";
	/**
	 * Supports RFC 2246: TLS version 1.0 ; may support other versions
	 */
	String TLSv1 = "TLSv1";
	/**
	 * Supports RFC 4346: TLS version 1.1 ; may support other versions
	 */
	String TLSv11 = "TLSv1.1";
	/**
	 * Supports RFC 5246: TLS version 1.2 ; may support other versions
	 */
	String TLSv12 = "TLSv1.2";
}
