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
package io.github.future0923.debug.tools.base.hutool.core.io.checksum;

import io.github.future0923.debug.tools.base.hutool.core.io.checksum.crc16.CRC16Checksum;
import io.github.future0923.debug.tools.base.hutool.core.io.checksum.crc16.CRC16IBM;

import java.io.Serializable;
import java.util.zip.Checksum;

/**
 * CRC16 循环冗余校验码（Cyclic Redundancy Check）实现，默认IBM算法
 *
 * @author looly
 * @since 4.4.1
 */
public class CRC16 implements Checksum, Serializable {
	private static final long serialVersionUID = 1L;

	private final CRC16Checksum crc16;

	public CRC16() {
		this(new CRC16IBM());
	}

	/**
	 * 构造
	 *
	 * @param crc16Checksum {@link CRC16Checksum} 实现
	 */
	public CRC16(CRC16Checksum crc16Checksum) {
		this.crc16 = crc16Checksum;
	}

	/**
	 * 获取16进制的CRC16值
	 *
	 * @return 16进制的CRC16值
	 * @since 5.7.22
	 */
	public String getHexValue() {
		return this.crc16.getHexValue();
	}

	/**
	 * 获取16进制的CRC16值
	 *
	 * @param isPadding 不足4位时，是否填充0以满足位数
	 * @return 16进制的CRC16值，4位
	 * @since 5.7.22
	 */
	public String getHexValue(boolean isPadding) {
		return crc16.getHexValue(isPadding);
	}

	@Override
	public long getValue() {
		return crc16.getValue();
	}

	@Override
	public void reset() {
		crc16.reset();
	}

	@Override
	public void update(byte[] b, int off, int len) {
		crc16.update(b, off, len);
	}

	@Override
	public void update(int b) {
		crc16.update(b);
	}
}
