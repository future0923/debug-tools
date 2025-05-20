package io.github.future0923.debug.tools.base.hutool.core.convert.impl;

import io.github.future0923.debug.tools.base.hutool.core.convert.AbstractConverter;
import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link AtomicBoolean}转换器
 *
 * @author Looly
 * @since 3.0.8
 */
public class AtomicBooleanConverter extends AbstractConverter<AtomicBoolean> {
	private static final long serialVersionUID = 1L;

	@Override
	protected AtomicBoolean convertInternal(Object value) {
		if (value instanceof Boolean) {
			return new AtomicBoolean((Boolean) value);
		}
		final String valueStr = convertToStr(value);
		return new AtomicBoolean(BooleanUtil.toBoolean(valueStr));
	}

}
