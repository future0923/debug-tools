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
package io.github.future0923.debug.tools.base.hutool.core.thread;


import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程拒绝策略枚举
 *
 * <p>
 * 如果设置了maxSize, 当总线程数达到上限, 会调用RejectedExecutionHandler进行处理，此枚举为JDK预定义的几种策略枚举表示
 *
 * @author looly
 * @since 4.1.13
 */
public enum RejectPolicy {

	/** 处理程序遭到拒绝将抛出RejectedExecutionException */
	ABORT(new ThreadPoolExecutor.AbortPolicy()),
	/** 放弃当前任务 */
	DISCARD(new ThreadPoolExecutor.DiscardPolicy()),
	/** 如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，然后重试执行程序（如果再次失败，则重复此过程） */
	DISCARD_OLDEST(new ThreadPoolExecutor.DiscardOldestPolicy()),
	/** 由主线程来直接执行 */
	CALLER_RUNS(new ThreadPoolExecutor.CallerRunsPolicy()),
	/** 当任务队列过长时处于阻塞状态，直到添加到队列中，固定并发数去访问，并且不希望丢弃任务时使用此策略 */
	BLOCK(new BlockPolicy());

	private final RejectedExecutionHandler value;

	RejectPolicy(RejectedExecutionHandler handler) {
		this.value = handler;
	}

	/**
	 * 获取RejectedExecutionHandler枚举值
	 *
	 * @return RejectedExecutionHandler
	 */
	public RejectedExecutionHandler getValue() {
		return this.value;
	}
}
