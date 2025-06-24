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

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * 账号密码形式的{@link Authenticator}
 *
 * @author looly
 * @since 5.7.2
 */
public class UserPassAuthenticator extends Authenticator {

	private final String user;
	private final char[] pass;

	/**
	 * 构造
	 *
	 * @param user 用户名
	 * @param pass 密码
	 */
	public UserPassAuthenticator(String user, char[] pass) {
		this.user = user;
		this.pass = pass;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(this.user, this.pass);
	}

}
