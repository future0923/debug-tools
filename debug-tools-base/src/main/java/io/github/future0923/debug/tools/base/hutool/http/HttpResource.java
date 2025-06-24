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
package io.github.future0923.debug.tools.base.hutool.http;

import io.github.future0923.debug.tools.base.hutool.core.io.resource.Resource;
import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

/**
 * HTTP资源，可自定义Content-Type
 *
 * @author looly
 * @since 5.7.17
 */
public class HttpResource implements Resource, Serializable {
	private static final long serialVersionUID = 1L;

	private final Resource resource;
	private final String contentType;

	/**
	 * 构造
	 *
	 * @param resource    资源，非空
	 * @param contentType Content-Type类型，{@code null}表示不设置
	 */
	public HttpResource(Resource resource, String contentType) {
		this.resource = Assert.notNull(resource, "Resource must be not null !");
		this.contentType = contentType;
	}

	@Override
	public String getName() {
		return resource.getName();
	}

	@Override
	public URL getUrl() {
		return resource.getUrl();
	}

	@Override
	public InputStream getStream() {
		return resource.getStream();
	}

	/**
	 * 获取自定义Content-Type类型
	 *
	 * @return Content-Type类型
	 */
	public String getContentType() {
		return this.contentType;
	}
}
