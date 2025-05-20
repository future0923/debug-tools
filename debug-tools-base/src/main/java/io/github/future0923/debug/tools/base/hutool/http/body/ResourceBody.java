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

import io.github.future0923.debug.tools.base.hutool.core.io.resource.Resource;

import java.io.OutputStream;

/**
 * {@link Resource}类型的Http request body，主要发送编码后的表单数据或rest body（如JSON或XML）
 *
 * @author looly
 * @since 5.8.13
 */
public class ResourceBody implements RequestBody {

	private final Resource resource;

	/**
	 * 创建 Http request body
	 *
	 * @param resource body内容，编码后
	 * @return BytesBody
	 */
	public static ResourceBody create(Resource resource) {
		return new ResourceBody(resource);
	}

	/**
	 * 构造
	 *
	 * @param resource Body内容，编码后
	 */
	public ResourceBody(Resource resource) {
		this.resource = resource;
	}

	@Override
	public void write(OutputStream out) {
		if(null != this.resource){
			this.resource.writeTo(out);
		}
	}

	@Override
	public String toString() {
		return this.resource.readUtf8Str();
	}
}
