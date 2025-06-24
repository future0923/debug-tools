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
package io.github.future0923.debug.tools.base.hutool.core.convert.impl;

import io.github.future0923.debug.tools.base.hutool.core.convert.AbstractConverter;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * URI对象转换器
 * @author Looly
 *
 */
public class URIConverter extends AbstractConverter<URI>{
	private static final long serialVersionUID = 1L;

	@Override
	protected URI convertInternal(Object value) {
		try {
			if(value instanceof File){
				return ((File)value).toURI();
			}

			if(value instanceof URL){
				return ((URL)value).toURI();
			}
			return new URI(convertToStr(value));
		} catch (Exception e) {
			// Ignore Exception
		}
		return null;
	}

}
