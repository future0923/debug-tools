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
package io.github.future0923.debug.tools.base.hutool.core.io.watch.watchers;

import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.watch.Watcher;
import io.github.future0923.debug.tools.base.hutool.core.lang.Chain;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Iterator;
import java.util.List;

/**
 * 观察者链<br>
 * 用于加入多个观察者
 *
 * @author Looly
 * @since 3.1.0
 */
public class WatcherChain implements Watcher, Chain<Watcher, WatcherChain>{

	/** 观察者列表 */
	final private List<Watcher> chain;

	/**
	 * 创建观察者链{@link WatcherChain}
	 * @param watchers  观察者列表
	 * @return {@link WatcherChain}
	 */
	public static WatcherChain create(Watcher... watchers) {
		return new WatcherChain(watchers);
	}

	/**
	 * 构造
	 * @param watchers 观察者列表
	 */
	public WatcherChain(Watcher... watchers) {
		chain = CollUtil.newArrayList(watchers);
	}

	@Override
	public void onCreate(WatchEvent<?> event, Path currentPath) {
		for (Watcher watcher : chain) {
			watcher.onCreate(event, currentPath);
		}
	}

	@Override
	public void onModify(WatchEvent<?> event, Path currentPath) {
		for (Watcher watcher : chain) {
			watcher.onModify(event, currentPath);
		}
	}

	@Override
	public void onDelete(WatchEvent<?> event, Path currentPath) {
		for (Watcher watcher : chain) {
			watcher.onDelete(event, currentPath);
		}
	}

	@Override
	public void onOverflow(WatchEvent<?> event, Path currentPath) {
		for (Watcher watcher : chain) {
			watcher.onOverflow(event, currentPath);
		}
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public Iterator<Watcher> iterator() {
		return this.chain.iterator();
	}

	@Override
	public WatcherChain addChain(Watcher element) {
		this.chain.add(element);
		return this;
	}

}
