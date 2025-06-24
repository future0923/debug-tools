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
package io.github.future0923.debug.tools.base.hutool.core.lang.hash;

import io.github.future0923.debug.tools.base.hutool.core.exceptions.UtilException;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Ketama算法，用于在一致性Hash中快速定位服务器位置
 *
 * @author looly
 * @since 5.7.20
 */
public class KetamaHash implements Hash64<String>, Hash32<String> {

	@Override
	public long hash64(String key) {
		byte[] bKey = md5(key);
		return ((long) (bKey[3] & 0xFF) << 24)
				| ((long) (bKey[2] & 0xFF) << 16)
				| ((long) (bKey[1] & 0xFF) << 8)
				| (bKey[0] & 0xFF);
	}

	@Override
	public int hash32(String key) {
		return (int) (hash64(key) & 0xffffffffL);
	}

	@Override
	public Number hash(String key) {
		return hash64(key);
	}

	/**
	 * 计算MD5值，使用UTF-8编码
	 *
	 * @param key 被计算的键
	 * @return MD5值
	 */
	private static byte[] md5(String key) {
		final MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new UtilException("MD5 algorithm not suooport!", e);
		}
		return md5.digest(StrUtil.utf8Bytes(key));
	}
}
