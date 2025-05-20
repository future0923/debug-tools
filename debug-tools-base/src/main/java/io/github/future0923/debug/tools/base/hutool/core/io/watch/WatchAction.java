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
package io.github.future0923.debug.tools.base.hutool.core.io.watch;


import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * 监听事件处理函数接口
 *
 * @author looly
 * @since 5.4.0
 */
@FunctionalInterface
public interface WatchAction {

	/**
	 * 事件处理，通过实现此方法处理各种事件。
	 *
	 * 事件可以调用 {@link WatchEvent#kind()}获取，对应事件见{@link WatchKind}
	 *
	 * @param event       事件
	 * @param currentPath 事件发生的当前Path路径
	 */
	void doAction(WatchEvent<?> event, Path currentPath);
}
