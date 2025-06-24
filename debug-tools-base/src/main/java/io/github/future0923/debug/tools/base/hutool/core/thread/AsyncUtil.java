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

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * {@link CompletableFuture}异步工具类<br>
 * {@link CompletableFuture} 是 Future 的改进，可以通过传入回调对象，在任务完成后调用之
 *
 * @author achao1441470436@gmail.com
 * @since 5.7.17
 */
public class AsyncUtil {

	/**
	 * 等待所有任务执行完毕，包裹了异常
	 *
	 * @param tasks 并行任务
	 * @throws UndeclaredThrowableException 未受检异常
	 */
	public static void waitAll(CompletableFuture<?>... tasks) {
		try {
			CompletableFuture.allOf(tasks).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new ThreadException(e);
		}
	}

	/**
	 * 等待任意一个任务执行完毕，包裹了异常
	 *
	 * @param <T>  任务返回值类型
	 * @param tasks 并行任务
	 * @return 执行结束的任务返回值
	 * @throws UndeclaredThrowableException 未受检异常
	 */
	@SuppressWarnings("unchecked")
	public static <T> T waitAny(CompletableFuture<?>... tasks) {
		try {
			return (T) CompletableFuture.anyOf(tasks).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new ThreadException(e);
		}
	}

	/**
	 * 获取异步任务结果，包裹了异常
	 *
	 * @param <T>  任务返回值类型
	 * @param task 异步任务
	 * @return 任务返回值
	 * @throws RuntimeException 未受检异常
	 */
	public static <T> T get(CompletableFuture<T> task) {
		try {
			return task.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new ThreadException(e);
		}
	}

}
