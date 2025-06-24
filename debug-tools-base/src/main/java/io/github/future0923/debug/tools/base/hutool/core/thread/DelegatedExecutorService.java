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

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * ExecutorService代理
 *
 * @author loolly
 */
public class DelegatedExecutorService extends AbstractExecutorService {
	private final ExecutorService e;

	/**
	 * 构造
	 *
	 * @param executor {@link ExecutorService}
	 */
	public DelegatedExecutorService(ExecutorService executor) {
		Assert.notNull(executor, "executor must be not null !");
		e = executor;
	}

	@Override
	public void execute(Runnable command) {
		e.execute(command);
	}

	@Override
	public void shutdown() {
		e.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return e.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return e.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return e.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return e.awaitTermination(timeout, unit);
	}

	@Override
	public Future<?> submit(Runnable task) {
		return e.submit(task);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return e.submit(task);
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return e.submit(task, result);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return e.invokeAll(tasks);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		return e.invokeAll(tasks, timeout, unit);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		return e.invokeAny(tasks);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return e.invokeAny(tasks, timeout, unit);
	}
}
