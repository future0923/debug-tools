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
 * CRC16_ANSI
 *
 * @author looly
 * @since 5.3.10
 */
public class CRC16Ansi extends CRC16Checksum{
	private static final long serialVersionUID = 1L;

	private static final int WC_POLY = 0xa001;

	@Override
	public void reset() {
		this.wCRCin = 0xffff;
	}

	@Override
	public void update(int b) {
		int hi = wCRCin >> 8;
		hi ^= b;
		wCRCin = hi;

		for (int i = 0; i < 8; i++) {
			int flag = wCRCin & 0x0001;
			wCRCin = wCRCin >> 1;
			if (flag == 1) {
				wCRCin ^= WC_POLY;
			}
		}
	}
}
