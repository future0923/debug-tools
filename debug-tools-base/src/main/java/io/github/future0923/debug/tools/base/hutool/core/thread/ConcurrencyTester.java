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

import io.github.future0923.debug.tools.base.hutool.core.date.TimeInterval;

import java.io.Closeable;
import java.io.IOException;

/**
 * 高并发测试工具类
 *
 * <pre>
 * ps:
 * //模拟1000个线程并发
 * ConcurrencyTester ct = new ConcurrencyTester(1000);
 * ct.test(() -&gt; {
 *      // 需要并发测试的业务代码
 * });
 *
 * Console.log(ct.getInterval());
 * ct.close();
 * </pre>
 *
 * @author kwer
 */
public class ConcurrencyTester implements Closeable {
	private final SyncFinisher sf;
	private final TimeInterval timeInterval;
	private long interval;

	/**
	 * 构造
	 * @param threadSize 线程数
	 */
	public ConcurrencyTester(int threadSize) {
		this.sf = new SyncFinisher(threadSize);
		this.timeInterval = new TimeInterval();
	}

	/**
	 * 执行测试<br>
	 * 执行测试后不会关闭线程池，可以调用{@link #close()}释放线程池
	 *
	 * @param runnable 要测试的内容
	 * @return this
	 */
	public ConcurrencyTester test(Runnable runnable) {
		this.sf.clearWorker();

		timeInterval.start();
		this.sf
				.addRepeatWorker(runnable)
				.setBeginAtSameTime(true)
				.start();

		this.interval = timeInterval.interval();
		return this;
	}

	/**
	 * 重置测试器，重置包括：
	 *
	 * <ul>
	 *     <li>清空worker</li>
	 *     <li>重置计时器</li>
	 * </ul>
	 *
	 * @return this
	 * @since 5.7.2
	 */
	public ConcurrencyTester reset(){
		this.sf.clearWorker();
		this.timeInterval.restart();
		return this;
	}

	/**
	 * 获取执行时间
	 *
	 * @return 执行时间，单位毫秒
	 */
	public long getInterval() {
		return this.interval;
	}

	@Override
	public void close() throws IOException {
		this.sf.close();
	}
}
