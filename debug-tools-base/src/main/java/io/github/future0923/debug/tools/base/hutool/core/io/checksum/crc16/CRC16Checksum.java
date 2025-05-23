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
package io.github.future0923.debug.tools.base.hutool.core.io.checksum.crc16;

import io.github.future0923.debug.tools.base.hutool.core.util.HexUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.io.Serializable;
import java.util.zip.Checksum;

/**
 * CRC16 Checksum，用于提供多种CRC16算法的通用实现<br>
 * 通过继承此类，重写update和reset完成相应算法。
 *
 * @author looly
 * @since 5.3.10
 */
public abstract class CRC16Checksum implements Checksum, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * CRC16 Checksum 结果值
	 */
	protected int wCRCin;

	public CRC16Checksum(){
		reset();
	}

	@Override
	public long getValue() {
		return wCRCin;
	}

	/**
	 * 获取16进制的CRC16值
	 *
	 * @return 16进制的CRC16值
	 */
	public String getHexValue(){
		return getHexValue(false);
	}

	/**
	 * 获取16进制的CRC16值
	 * @param isPadding 不足4位时，是否填充0以满足位数
	 * @return 16进制的CRC16值，4位
	 */
	public String getHexValue(boolean isPadding){
		String hex = HexUtil.toHex(getValue());
		if(isPadding){
			hex = StrUtil.padPre(hex, 4, '0');
		}

		return hex;
	}

	@Override
	public void reset() {
		wCRCin = 0x0000;
	}

	/**
	 * 计算全部字节
	 * @param b 字节
	 */
	public void update(byte[] b){
		update(b, 0, b.length);
	}

	@Override
	public void update(byte[] b, int off, int len) {
		for (int i = off; i < off + len; i++)
			update(b[i]);
	}

}
