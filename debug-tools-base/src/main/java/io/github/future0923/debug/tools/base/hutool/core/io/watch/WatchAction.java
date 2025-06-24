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
 * 监听事件处理函数接口
 *
 * @author looly
 * @since 5.4.0
 */
@FunctionalInterface
public interface WatchAction {

	/**
	 * 事件处理，通过实现此方法处理各种事件。
	 *
	 * 事件可以调用 {@link WatchEvent#kind()}获取，对应事件见{@link WatchKind}
	 *
	 * @param event       事件
	 * @param currentPath 事件发生的当前Path路径
	 */
	void doAction(WatchEvent<?> event, Path currentPath);
}
