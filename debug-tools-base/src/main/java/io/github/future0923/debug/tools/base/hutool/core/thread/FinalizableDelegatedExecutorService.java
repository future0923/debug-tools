package io.github.future0923.debug.tools.base.hutool.core.thread;

import java.util.concurrent.ExecutorService;

/**
 * 保证ExecutorService在对象回收时正常结束
 *
 * @author loolly
 */
public class FinalizableDelegatedExecutorService extends DelegatedExecutorService {

	/**
	 * 构造
	 *
	 * @param executor {@link ExecutorService}
	 */
	FinalizableDelegatedExecutorService(ExecutorService executor) {
		super(executor);
	}

	@Override
	protected void finalize() {
		super.shutdown();
	}
}