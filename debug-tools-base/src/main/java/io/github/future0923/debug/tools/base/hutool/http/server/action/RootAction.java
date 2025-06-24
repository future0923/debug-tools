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
package io.github.future0923.debug.tools.base.hutool.http.server.action;

import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.http.server.HttpServerRequest;
import io.github.future0923.debug.tools.base.hutool.http.server.HttpServerResponse;

import java.io.File;
import java.util.List;

/**
 * 默认的处理器，通过解析用户传入的path，找到网页根目录下对应文件后返回
 *
 * @author looly
 * @since 5.2.6
 */
public class RootAction implements Action {

	public static final String DEFAULT_INDEX_FILE_NAME = "index.html";

	private final File rootDir;
	private final List<String> indexFileNames;

	/**
	 * 构造
	 *
	 * @param rootDir 网页根目录
	 */
	public RootAction(String rootDir) {
		this(new File(rootDir));
	}

	/**
	 * 构造
	 *
	 * @param rootDir 网页根目录
	 */
	public RootAction(File rootDir) {
		this(rootDir, DEFAULT_INDEX_FILE_NAME);
	}

	/**
	 * 构造
	 *
	 * @param rootDir        网页根目录
	 * @param indexFileNames 主页文件名列表
	 */
	public RootAction(String rootDir, String... indexFileNames) {
		this(new File(rootDir), indexFileNames);
	}

	/**
	 * 构造
	 *
	 * @param rootDir        网页根目录
	 * @param indexFileNames 主页文件名列表
	 * @since 5.4.0
	 */
	public RootAction(File rootDir, String... indexFileNames) {
		this.rootDir = rootDir;
		this.indexFileNames = CollUtil.toList(indexFileNames);
	}

	@Override
	public void doAction(HttpServerRequest request, HttpServerResponse response) {
		final String path = request.getPath();

		File file = FileUtil.file(rootDir, path);
		if (file.exists()) {
			if (file.isDirectory()) {
				for (String indexFileName : indexFileNames) {
					//默认读取主页
					file = FileUtil.file(file, indexFileName);
					if (file.exists() && file.isFile()) {
						response.write(file);
						return;
					}
				}
			} else{
				final String name = request.getParam("name");
				response.write(file, name);
				return;
			}
		}

		response.send404("404 Not Found !");
	}
}
