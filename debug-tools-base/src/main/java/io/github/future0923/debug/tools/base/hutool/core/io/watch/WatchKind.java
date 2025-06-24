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

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

/**
 * 监听事件类型枚举，包括：
 *
 * <pre>
 *      1. 事件丢失 OVERFLOW -》StandardWatchEventKinds.OVERFLOW
 *      2. 修改事件 MODIFY   -》StandardWatchEventKinds.ENTRY_MODIFY
 *      3. 创建事件 CREATE   -》StandardWatchEventKinds.ENTRY_CREATE
 *      4. 删除事件 DELETE   -》StandardWatchEventKinds.ENTRY_DELETE
 * </pre>
 *
 * @author loolly
 * @since 5.1.0
 */
public enum WatchKind {

	/**
	 * 事件丢失
	 */
	OVERFLOW(StandardWatchEventKinds.OVERFLOW),
	/**
	 * 修改事件
	 */
	MODIFY(StandardWatchEventKinds.ENTRY_MODIFY),
	/**
	 * 创建事件
	 */
	CREATE(StandardWatchEventKinds.ENTRY_CREATE),
	/**
	 * 删除事件
	 */
	DELETE(StandardWatchEventKinds.ENTRY_DELETE);

	/**
	 * 全部事件
	 */
	public static final WatchEvent.Kind<?>[] ALL = {//
			OVERFLOW.getValue(),      //事件丢失
			MODIFY.getValue(), //修改
			CREATE.getValue(),  //创建
			DELETE.getValue()   //删除
	};

	private final WatchEvent.Kind<?> value;

	/**
	 * 构造
	 *
	 * @param value 事件类型
	 */
	WatchKind(WatchEvent.Kind<?> value) {
		this.value = value;
	}

	/**
	 * 获取枚举对应的事件类型
	 *
	 * @return 事件类型值
	 */
	public WatchEvent.Kind<?> getValue() {
		return this.value;
	}
}
