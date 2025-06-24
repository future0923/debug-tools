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

import java.io.File;
import java.util.Collection;

/**
 * 多文件组合资源<br>
 * 此资源为一个利用游标自循环资源，只有调用{@link #next()} 方法才会获取下一个资源，使用完毕后调用{@link #reset()}方法重置游标
 *
 * @author looly
 *
 */
public class MultiFileResource extends MultiResource{
	private static final long serialVersionUID = 1L;

	/**
	 * 构造
	 *
	 * @param files 文件资源列表
	 */
	public MultiFileResource(Collection<File> files) {
		add(files);
	}

	/**
	 * 构造
	 *
	 * @param files 文件资源列表
	 */
	public MultiFileResource(File... files) {
		add(files);
	}

	/**
	 * 增加文件资源
	 *
	 * @param files 文件资源
	 * @return this
	 */
	public MultiFileResource add(File... files) {
		for (File file : files) {
			this.add(new FileResource(file));
		}
		return this;
	}

	/**
	 * 增加文件资源
	 *
	 * @param files 文件资源
	 * @return this
	 */
	public MultiFileResource add(Collection<File> files) {
		for (File file : files) {
			this.add(new FileResource(file));
		}
		return this;
	}

	@Override
	public MultiFileResource add(Resource resource) {
		return (MultiFileResource)super.add(resource);
	}
}
