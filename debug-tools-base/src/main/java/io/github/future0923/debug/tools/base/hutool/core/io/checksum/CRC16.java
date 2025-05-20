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
