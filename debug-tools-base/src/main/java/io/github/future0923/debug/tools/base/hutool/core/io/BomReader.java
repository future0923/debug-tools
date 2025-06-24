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
package io.github.future0923.debug.tools.base.hutool.core.io;

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * 读取带BOM头的流内容的Reader，如果非bom的流或无法识别的编码，则默认UTF-8<br>
 * BOM定义：http://www.unicode.org/unicode/faq/utf_bom.html
 *
 * <ul>
 * <li>00 00 FE FF = UTF-32, big-endian</li>
 * <li>FF FE 00 00 = UTF-32, little-endian</li>
 * <li>EF BB BF = UTF-8</li>
 * <li>FE FF = UTF-16, big-endian</li>
 * <li>FF FE = UTF-16, little-endian</li>
 * </ul>
 * 使用： <br>
 * <code>
 * FileInputStream fis = new FileInputStream(file); <br>
 * BomReader uin = new BomReader(fis); <br>
 * </code>
 *
 * @author looly
 * @since 5.7.14
 */
public class BomReader extends Reader {

	private InputStreamReader reader;

	/**
	 * 构造
	 *
	 * @param in 流
	 */
	public BomReader(InputStream in) {
		Assert.notNull(in, "InputStream must be not null!");
		final BOMInputStream bin = (in instanceof BOMInputStream) ? (BOMInputStream) in : new BOMInputStream(in);
		try {
			this.reader = new InputStreamReader(bin, bin.getCharset());
		} catch (UnsupportedEncodingException ignore) {
		}
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		return reader.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}
