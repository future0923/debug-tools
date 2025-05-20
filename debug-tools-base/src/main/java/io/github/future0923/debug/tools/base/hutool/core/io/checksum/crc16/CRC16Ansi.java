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
