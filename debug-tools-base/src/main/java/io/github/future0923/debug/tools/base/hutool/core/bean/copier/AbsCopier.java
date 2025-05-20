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
package io.github.future0923.debug.tools.base.hutool.core.bean.copier;

import io.github.future0923.debug.tools.base.hutool.core.lang.copier.Copier;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;

/**
 * 抽象的对象拷贝封装，提供来源对象、目标对象持有
 *
 * @param <S> 来源对象类型
 * @param <T> 目标对象类型
 * @author looly
 * @since 5.8.0
 */
public abstract class AbsCopier<S, T> implements Copier<T> {

	protected final S source;
	protected final T target;
	/**
	 * 拷贝选项
	 */
	protected final CopyOptions copyOptions;

	public AbsCopier(S source, T target, CopyOptions copyOptions) {
		this.source = source;
		this.target = target;
		this.copyOptions = ObjectUtil.defaultIfNull(copyOptions, CopyOptions::create);
	}
}
