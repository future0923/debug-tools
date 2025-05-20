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
package io.github.future0923.debug.tools.base.hutool.core.lang.generator;

import io.github.future0923.debug.tools.base.hutool.core.lang.Snowflake;

/**
 * Snowflake生成器<br>
 * 注意，默认此生成器必须单例使用，否则会有重复<br>
 * 默认构造的终端ID和数据中心ID都为0，不适用于分布式环境。
 *
 * @author looly
 * @since 5.4.3
 */
public class SnowflakeGenerator implements Generator<Long> {

	private final Snowflake snowflake;

	/**
	 * 构造
	 */
	public SnowflakeGenerator() {
		this(0, 0);
	}

	/**
	 * 构造
	 *
	 * @param workerId     终端ID
	 * @param dataCenterId 数据中心ID
	 */
	public SnowflakeGenerator(long workerId, long dataCenterId) {
		snowflake = new Snowflake(workerId, dataCenterId);
	}

	@Override
	public Long next() {
		return this.snowflake.nextId();
	}
}
