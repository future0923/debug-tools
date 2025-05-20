/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
