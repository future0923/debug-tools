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
package io.github.future0923.debug.tools.base.hutool.core.lang;

import io.github.future0923.debug.tools.base.hutool.core.exceptions.UtilException;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.lang.management.ManagementFactory;

/**
 * 进程ID单例封装<br>
 * 第一次访问时调用{@link ManagementFactory#getRuntimeMXBean()}获取PID信息，之后直接使用缓存值
 *
 * @author looly
 * @since 5.8.0
 */
public enum Pid {
	INSTANCE;

	private final int pid;

	Pid() {
		this.pid = getPid();
	}

	/**
	 * 获取PID值
	 *
	 * @return pid
	 */
	public int get() {
		return this.pid;
	}

	/**
	 * 获取当前进程ID，首先获取进程名称，读取@前的ID值，如果不存在，则读取进程名的hash值
	 *
	 * @return 进程ID
	 * @throws UtilException 进程名称为空
	 */
	private static int getPid() throws UtilException {
		final String processName = ManagementFactory.getRuntimeMXBean().getName();
		if (StrUtil.isBlank(processName)) {
			throw new UtilException("Process name is blank!");
		}
		final int atIndex = processName.indexOf('@');
		if (atIndex > 0) {
			return Integer.parseInt(processName.substring(0, atIndex));
		} else {
			return processName.hashCode();
		}
	}
}
