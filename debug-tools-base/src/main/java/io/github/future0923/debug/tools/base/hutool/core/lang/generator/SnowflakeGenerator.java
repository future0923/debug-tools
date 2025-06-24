/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
