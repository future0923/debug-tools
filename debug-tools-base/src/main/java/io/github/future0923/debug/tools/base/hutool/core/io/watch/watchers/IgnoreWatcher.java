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
