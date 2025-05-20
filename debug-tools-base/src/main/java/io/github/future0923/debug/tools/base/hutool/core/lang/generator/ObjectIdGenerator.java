package io.github.future0923.debug.tools.base.hutool.core.lang.generator;

import io.github.future0923.debug.tools.base.hutool.core.lang.ObjectId;

/**
 * ObjectId生成器
 *
 * @author looly
 * @since 5.4.3
 */
public class ObjectIdGenerator implements Generator<String> {
	@Override
	public String next() {
		return ObjectId.next();
	}
}
