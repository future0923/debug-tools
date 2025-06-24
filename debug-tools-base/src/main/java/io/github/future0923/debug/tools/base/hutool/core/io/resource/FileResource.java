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
package io.github.future0923.debug.tools.base.hutool.core.io.resource;

import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.URLUtil;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;

/**
 * 文件资源访问对象，支持{@link Path} 和 {@link File} 访问
 *
 * @author looly
 */
public class FileResource implements Resource, Serializable {
	private static final long serialVersionUID = 1L;

	private final File file;
	private final long lastModified;
	private final String name;

	// ----------------------------------------------------------------------- Constructor start
	/**
	 * 构造
	 *
	 * @param path 文件绝对路径或相对ClassPath路径，但是这个路径不能指向一个jar包中的文件
	 */
	public FileResource(String path) {
		this(FileUtil.file(path));
	}

	/**
	 * 构造，文件名使用文件本身的名字，带扩展名
	 *
	 * @param path 文件
	 * @since 4.4.1
	 */
	public FileResource(Path path) {
		this(path.toFile());
	}

	/**
	 * 构造，文件名使用文件本身的名字，带扩展名
	 *
	 * @param file 文件
	 */
	public FileResource(File file) {
		this(file, null);
	}

	/**
	 * 构造
	 *
	 * @param file 文件
	 * @param fileName 文件名，带扩展名，如果为null获取文件本身的文件名
	 */
	public FileResource(File file, String fileName) {
		Assert.notNull(file, "File must be not null !");
		this.file = file;
		this.lastModified = file.lastModified();
		this.name = ObjectUtil.defaultIfNull(fileName, file::getName);
	}

	// ----------------------------------------------------------------------- Constructor end

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public URL getUrl(){
		return URLUtil.getURL(this.file);
	}

	@Override
	public InputStream getStream() throws NoResourceException {
		if (!this.file.exists()) {
			throw new NoResourceException("File [{}] not exist!", this.file.getAbsolutePath());
		}
		return FileUtil.getInputStream(this.file);
	}

	/**
	 * 获取文件
	 *
	 * @return 文件
	 */
	public File getFile() {
		return this.file;
	}

	@Override
	public boolean isModified() {
		return this.lastModified != file.lastModified();
	}

	/**
	 * 返回路径
	 * @return 返回URL路径
	 */
	@Override
	public String toString() {
		return this.file.toString();
	}
}
