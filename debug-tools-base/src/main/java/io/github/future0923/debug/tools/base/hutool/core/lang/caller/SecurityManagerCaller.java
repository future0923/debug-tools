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
package io.github.future0923.debug.tools.base.hutool.core.lang.caller;

import io.github.future0923.debug.tools.base.hutool.core.util.ArrayUtil;

import java.io.Serializable;

/**
 * {@link SecurityManager} 方式获取调用者
 *
 * @author Looly
 */
public class SecurityManagerCaller extends SecurityManager implements Caller, Serializable {
	private static final long serialVersionUID = 1L;

	private static final int OFFSET = 1;

	@Override
	public Class<?> getCaller() {
		final Class<?>[] context = getClassContext();
		if (null != context && (OFFSET + 1) < context.length) {
			return context[OFFSET + 1];
		}
		return null;
	}

	@Override
	public Class<?> getCallerCaller() {
		final Class<?>[] context = getClassContext();
		if (null != context && (OFFSET + 2) < context.length) {
			return context[OFFSET + 2];
		}
		return null;
	}

	@Override
	public Class<?> getCaller(int depth) {
		final Class<?>[] context = getClassContext();
		if (null != context && (OFFSET + depth) < context.length) {
			return context[OFFSET + depth];
		}
		return null;
	}

	@Override
	public boolean isCalledBy(Class<?> clazz) {
		final Class<?>[] classes = getClassContext();
		if(ArrayUtil.isNotEmpty(classes)) {
			for (Class<?> contextClass : classes) {
				if (contextClass.equals(clazz)) {
					return true;
				}
			}
		}
		return false;
	}
}
