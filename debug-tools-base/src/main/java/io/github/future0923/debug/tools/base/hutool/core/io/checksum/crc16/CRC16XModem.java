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
package io.github.future0923.debug.tools.base.hutool.core.io.checksum.crc16;


/**
 * CRC-CCITT (XModem)
 * CRC16_XMODEM：多项式x16+x12+x5+1（0x1021），初始值0x0000，低位在后，高位在前，结果与0x0000异或
 *
 * @author looly
 * @since 5.3.10
 */
public class CRC16XModem extends CRC16Checksum {
	private static final long serialVersionUID = 1L;

	// 0001 0000 0010 0001 (0, 5, 12)
	private static final int WC_POLY = 0x1021;

	@Override
	public void update(byte[] b, int off, int len) {
		super.update(b, off, len);
		wCRCin &= 0xffff;
	}

	@Override
	public void update(int b) {
		for (int i = 0; i < 8; i++) {
			boolean bit = ((b >> (7 - i) & 1) == 1);
			boolean c15 = ((wCRCin >> 15 & 1) == 1);
			wCRCin <<= 1;
			if (c15 ^ bit)
				wCRCin ^= WC_POLY;
		}
	}
}
