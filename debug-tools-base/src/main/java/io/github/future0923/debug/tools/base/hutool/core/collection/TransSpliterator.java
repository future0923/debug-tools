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

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 使用给定的转换函数，转换源{@link Spliterator}为新类型的{@link Spliterator}
 *
 * @param <F> 源元素类型
 * @param <T> 目标元素类型
 * @author looly
 * @since 5.4.3
 */
public class TransSpliterator<F, T> implements Spliterator<T> {
	private final Spliterator<F> fromSpliterator;
	private final Function<? super F, ? extends T> function;

	public TransSpliterator(Spliterator<F> fromSpliterator, Function<? super F, ? extends T> function) {
		this.fromSpliterator = fromSpliterator;
		this.function = function;
	}

	@Override
	public boolean tryAdvance(Consumer<? super T> action) {
		return fromSpliterator.tryAdvance(
				fromElement -> action.accept(function.apply(fromElement)));
	}

	@Override
	public void forEachRemaining(Consumer<? super T> action) {
		fromSpliterator.forEachRemaining(fromElement -> action.accept(function.apply(fromElement)));
	}

	@Override
	public Spliterator<T> trySplit() {
		Spliterator<F> fromSplit = fromSpliterator.trySplit();
		return (fromSplit != null) ? new TransSpliterator<>(fromSplit, function) : null;
	}

	@Override
	public long estimateSize() {
		return fromSpliterator.estimateSize();
	}

	@Override
	public int characteristics() {
		return fromSpliterator.characteristics()
				& ~(Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.SORTED);
	}
}
