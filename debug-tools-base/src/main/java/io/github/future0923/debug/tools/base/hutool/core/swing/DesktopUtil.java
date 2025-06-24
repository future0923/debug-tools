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
package io.github.future0923.debug.tools.base.hutool.core.swing;

import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.util.URLUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * 桌面相关工具（平台相关）<br>
 * Desktop 类允许 Java 应用程序启动已在本机桌面上注册的关联应用程序，以处理 URI 或文件。
 *
 * @author looly
 * @since 4.5.7
 */
public class DesktopUtil {

	/**
	 * 获得{@link Desktop}
	 *
	 * @return {@link Desktop}
	 */
	public static Desktop getDsktop() {
		return Desktop.getDesktop();
	}

	/**
	 * 使用平台默认浏览器打开指定URL地址
	 *
	 * @param url URL地址
	 */
	public static void browse(String url) {
		browse(URLUtil.toURI(url));
	}

	/**
	 * 使用平台默认浏览器打开指定URI地址
	 *
	 * @param uri URI地址
	 * @since 4.6.3
	 */
	public static void browse(URI uri) {
		final Desktop dsktop = getDsktop();
		try {
			dsktop.browse(uri);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 启动关联应用程序来打开文件
	 *
	 * @param file URL地址
	 */
	public static void open(File file) {
		final Desktop dsktop = getDsktop();
		try {
			dsktop.open(file);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 启动关联编辑器应用程序并打开用于编辑的文件
	 *
	 * @param file 文件
	 */
	public static void edit(File file) {
		final Desktop dsktop = getDsktop();
		try {
			dsktop.edit(file);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 使用关联应用程序的打印命令, 用本机桌面打印设备来打印文件
	 *
	 * @param file 文件
	 */
	public static void print(File file) {
		final Desktop dsktop = getDsktop();
		try {
			dsktop.print(file);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 使用平台默认浏览器打开指定URL地址
	 *
	 * @param mailAddress 邮件地址
	 */
	public static void mail(String mailAddress) {
		final Desktop dsktop = getDsktop();
		try {
			dsktop.mail(URLUtil.toURI(mailAddress));
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}
}
