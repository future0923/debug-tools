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
package io.github.future0923.debug.tools.base.hutool.core.io.file;

import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.map.MapUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.CharsetUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * {@link FileSystem}相关工具类封装<br>
 * 参考：https://blog.csdn.net/j16421881/article/details/78858690
 *
 * @author looly
 * @since 5.7.15
 */
public class FileSystemUtil {

	/**
	 * 创建 {@link FileSystem}
	 *
	 * @param path 文件路径，可以是目录或Zip文件等
	 * @return {@link FileSystem}
	 */
	public static FileSystem create(String path) {
		try {
			return FileSystems.newFileSystem(
					Paths.get(path).toUri(),
					MapUtil.of("create", "true"));
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 创建 Zip的{@link FileSystem}，默认UTF-8编码
	 *
	 * @param path 文件路径，可以是目录或Zip文件等
	 * @return {@link FileSystem}
	 */
	public static FileSystem createZip(String path) {
		return createZip(path, null);
	}

	/**
	 * 创建 Zip的{@link FileSystem}
	 *
	 * @param path 文件路径，可以是目录或Zip文件等
	 * @param charset 编码
	 * @return {@link FileSystem}
	 */
	public static FileSystem createZip(String path, Charset charset) {
		if(null == charset){
			charset = CharsetUtil.CHARSET_UTF_8;
		}
		final HashMap<String, String> env = new HashMap<>();
		env.put("create", "true");
		env.put("encoding", charset.name());

		try {
			return FileSystems.newFileSystem(
					URI.create("jar:" + Paths.get(path).toUri()), env);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 获取目录的根路径，或Zip文件中的根路径
	 *
	 * @param fileSystem {@link FileSystem}
	 * @return 根 {@link Path}
	 */
	public static Path getRoot(FileSystem fileSystem) {
		return fileSystem.getPath(StrUtil.SLASH);
	}
}
