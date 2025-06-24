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
package io.github.future0923.debug.tools.base.hutool.core.net;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本地端口生成器<br>
 * 用于生成本地可用（未被占用）的端口号<br>
 * 注意：多线程甚至单线程访问时可能会返回同一端口（例如获取了端口但是没有使用）
 *
 * @author looly
 * @since 4.0.3
 *
 */
public class LocalPortGenerater implements Serializable{
	private static final long serialVersionUID = 1L;

	/** 备选的本地端口 */
	private final AtomicInteger alternativePort;

	/**
	 * 构造
	 *
	 * @param beginPort 起始端口号
	 */
	public LocalPortGenerater(int beginPort) {
		alternativePort = new AtomicInteger(beginPort);
	}

	/**
	 * 生成一个本地端口，用于远程端口映射
	 *
	 * @return 未被使用的本地端口
	 */
	public int generate() {
		int validPort = alternativePort.get();
		// 获取可用端口
		while (false == NetUtil.isUsableLocalPort(validPort)) {
			validPort = alternativePort.incrementAndGet();
		}
		return validPort;
	}
}
