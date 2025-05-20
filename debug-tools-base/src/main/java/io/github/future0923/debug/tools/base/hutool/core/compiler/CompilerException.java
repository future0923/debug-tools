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
package io.github.future0923.debug.tools.base.hutool.core.compiler;

import io.github.future0923.debug.tools.base.hutool.core.exceptions.ExceptionUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

/**
 * 编译异常
 *
 * @author looly
 * @since 5.5.2
 */
public class CompilerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CompilerException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public CompilerException(String message) {
		super(message);
	}

	public CompilerException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public CompilerException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public CompilerException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
