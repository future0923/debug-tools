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
package io.github.future0923.debug.tools.base.hutool.core.io.file.visitor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 删除操作的FileVisitor实现，用于递归遍历删除文件夹
 *
 * @author looly
 * @since 5.5.1
 */
public class DelVisitor extends SimpleFileVisitor<Path> {

	public static DelVisitor INSTANCE = new DelVisitor();

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Files.delete(file);
		return FileVisitResult.CONTINUE;
	}

	/**
	 * 访问目录结束后删除目录，当执行此方法时，子文件或目录都已访问（删除）完毕<br>
	 * 理论上当执行到此方法时，目录下已经被清空了
	 *
	 * @param dir 目录
	 * @param e   异常
	 * @return {@link FileVisitResult}
	 * @throws IOException IO异常
	 */
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
		if (e == null) {
			Files.delete(dir);
			return FileVisitResult.CONTINUE;
		} else {
			throw e;
		}
	}
}
