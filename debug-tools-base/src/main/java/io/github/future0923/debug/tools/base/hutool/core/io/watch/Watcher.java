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
package io.github.future0923.debug.tools.base.hutool.core.io.watch;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * 观察者（监视器）
 *
 * @author Looly
 */
public interface Watcher {
	/**
	 * 文件创建时执行的方法
	 *
	 * @param event       事件
	 * @param currentPath 事件发生的当前Path路径
	 */
	void onCreate(WatchEvent<?> event, Path currentPath);

	/**
	 * 文件修改时执行的方法<br>
	 * 文件修改可能触发多次
	 *
	 * @param event       事件
	 * @param currentPath 事件发生的当前Path路径
	 */
	void onModify(WatchEvent<?> event, Path currentPath);

	/**
	 * 文件删除时执行的方法
	 *
	 * @param event       事件
	 * @param currentPath 事件发生的当前Path路径
	 */
	void onDelete(WatchEvent<?> event, Path currentPath);

	/**
	 * 事件丢失或出错时执行的方法
	 *
	 * @param event       事件
	 * @param currentPath 事件发生的当前Path路径
	 */
	void onOverflow(WatchEvent<?> event, Path currentPath);
}
