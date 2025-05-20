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
package io.github.future0923.debug.tools.base.hutool.http.body;

import io.github.future0923.debug.tools.base.hutool.core.io.IoUtil;

import java.io.OutputStream;

/**
 * bytes类型的Http request body，主要发送编码后的表单数据或rest body（如JSON或XML）
 *
 * @since 5.7.17
 * @author looly
 */
public class BytesBody implements RequestBody {

	private final byte[] content;

	/**
	 * 创建 Http request body
	 * @param content body内容，编码后
	 * @return BytesBody
	 */
	public static BytesBody create(byte[] content){
		return new BytesBody(content);
	}

	/**
	 * 构造
	 *
	 * @param content Body内容，编码后
	 */
	public BytesBody(byte[] content) {
		this.content = content;
	}

	@Override
	public void write(OutputStream out) {
		IoUtil.write(out, false, content);
	}
}
