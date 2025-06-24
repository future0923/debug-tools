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
