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
package io.github.future0923.debug.tools.base.hutool.core.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 此OutputStream写出数据到<b>/dev/null</b>，即忽略所有数据<br>
 * 来自 Apache Commons io
 *
 * @author looly
 * @since 4.0.6
 */
public class NullOutputStream extends OutputStream {

	/**
	 * 单例
	 */
	public static final NullOutputStream NULL_OUTPUT_STREAM = new NullOutputStream();

	/**
	 * 什么也不做，写出到<code>/dev/null</code>.
	 *
	 * @param b 写出的数据
	 * @param off 开始位置
	 * @param len 长度
	 */
	@Override
	public void write(byte[] b, int off, int len) {
		// to /dev/null
	}

	/**
	 * 什么也不做，写出到 <code>/dev/null</code>.
	 *
	 * @param b 写出的数据
	 */
	@Override
	public void write(int b) {
		// to /dev/null
	}

	/**
	 * 什么也不做，写出到 <code>/dev/null</code>.
	 *
	 * @param b 写出的数据
	 * @throws IOException 不抛出
	 */
	@Override
	public void write(byte[] b) throws IOException {
		// to /dev/null
	}

}
