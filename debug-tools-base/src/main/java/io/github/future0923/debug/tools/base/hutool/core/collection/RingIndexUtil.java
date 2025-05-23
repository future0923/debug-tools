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
package io.github.future0923.debug.tools.base.hutool.core.collection;

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 集合索引环形获取工具类
 *
 * @author ZhouChuGang
 * @since 5.7.15
 */
public class RingIndexUtil {

	/**
	 * 通过cas操作 实现对指定值内的回环累加
	 *
	 * @param object        集合
	 *                      <ul>
	 *                      <li>Collection - the collection size
	 *                      <li>Map - the map size
	 *                      <li>Array - the array size
	 *                      <li>Iterator - the number of elements remaining in the iterator
	 *                      <li>Enumeration - the number of elements remaining in the enumeration
	 *                      </ul>
	 * @param atomicInteger 原子操作类
	 * @return 索引位置
	 */
	public static int ringNextIntByObj(Object object, AtomicInteger atomicInteger) {
		Assert.notNull(object);
		int modulo = CollUtil.size(object);
		return ringNextInt(modulo, atomicInteger);
	}

	/**
	 * 通过cas操作 实现对指定值内的回环累加
	 *
	 * @param modulo        回环周期值
	 * @param atomicInteger 原子操作类
	 * @return 索引位置
	 */
	public static int ringNextInt(int modulo, AtomicInteger atomicInteger) {
		Assert.notNull(atomicInteger);
		Assert.isTrue(modulo > 0);
		if (modulo <= 1) {
			return 0;
		}
		for (; ; ) {
			int current = atomicInteger.get();
			int next = (current + 1) % modulo;
			if (atomicInteger.compareAndSet(current, next)) {
				return next;
			}
		}
	}

	/**
	 * 通过cas操作 实现对指定值内的回环累加<br>
	 * 此方法一般用于大量数据完成回环累加（如数据库中的值大于int最大值）
	 *
	 * @param modulo     回环周期值
	 * @param atomicLong 原子操作类
	 * @return 索引位置
	 */
	public static long ringNextLong(long modulo, AtomicLong atomicLong) {
		Assert.notNull(atomicLong);
		Assert.isTrue(modulo > 0);
		if (modulo <= 1) {
			return 0;
		}
		for (; ; ) {
			long current = atomicLong.get();
			long next = (current + 1) % modulo;
			if (atomicLong.compareAndSet(current, next)) {
				return next;
			}
		}
	}
}
