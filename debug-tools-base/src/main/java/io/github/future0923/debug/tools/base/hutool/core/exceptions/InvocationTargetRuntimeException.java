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
package io.github.future0923.debug.tools.base.hutool.core.exceptions;


/**
 * InvocationTargetException的运行时异常
 *
 * @author looly
 * @since 5.8.1
 */
public class InvocationTargetRuntimeException extends UtilException {

	public InvocationTargetRuntimeException(Throwable e) {
		super(e);
	}

	public InvocationTargetRuntimeException(String message) {
		super(message);
	}

	public InvocationTargetRuntimeException(String messageTemplate, Object... params) {
		super(messageTemplate, params);
	}

	public InvocationTargetRuntimeException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public InvocationTargetRuntimeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public InvocationTargetRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
		super(throwable, messageTemplate, params);
	}
}
