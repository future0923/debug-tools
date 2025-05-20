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
package io.github.future0923.debug.tools.base.hutool.core.io.resource;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

/**
 * 基于{@link InputStream}的资源获取器<br>
 * 注意：此对象中getUrl方法始终返回null
 *
 * @author looly
 * @since 4.0.9
 */
public class InputStreamResource implements Resource, Serializable {
	private static final long serialVersionUID = 1L;

	private final InputStream in;
	private final String name;

	/**
	 * 构造
	 *
	 * @param in {@link InputStream}
	 */
	public InputStreamResource(InputStream in) {
		this(in, null);
	}

	/**
	 * 构造
	 *
	 * @param in {@link InputStream}
	 * @param name 资源名称
	 */
	public InputStreamResource(InputStream in, String name) {
		this.in = in;
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public URL getUrl() {
		return null;
	}

	@Override
	public InputStream getStream() {
		return this.in;
	}
}
