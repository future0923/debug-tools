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

import io.github.future0923.debug.tools.base.hutool.core.io.watch.Watcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * 跳过所有事件处理Watcher<br>
 * 用户继承此类后实现需要监听的方法
 *
 * @author Looly
 * @since 3.1.0
 */
public class IgnoreWatcher implements Watcher {

	@Override
	public void onCreate(WatchEvent<?> event, Path currentPath) {
	}

	@Override
	public void onModify(WatchEvent<?> event, Path currentPath) {
	}

	@Override
	public void onDelete(WatchEvent<?> event, Path currentPath) {
	}

	@Override
	public void onOverflow(WatchEvent<?> event, Path currentPath) {
	}
}
