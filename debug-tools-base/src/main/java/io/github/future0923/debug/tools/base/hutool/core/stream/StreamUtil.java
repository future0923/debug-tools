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
package io.github.future0923.debug.tools.base.hutool.core.stream;

import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;
import io.github.future0923.debug.tools.base.hutool.core.util.CharsetUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * {@link Stream} 工具类
 *
 * @author looly
 * @since 5.6.7
 */
public class StreamUtil {

	@SafeVarargs
	public static <T> Stream<T> of(T... array) {
		Assert.notNull(array, "Array must be not null!");
		return Stream.of(array);
	}

	/**
	 * {@link Iterable}转换为{@link Stream}，默认非并行
	 *
	 * @param iterable 集合
	 * @param <T>      集合元素类型
	 * @return {@link Stream}
	 */
	public static <T> Stream<T> of(Iterable<T> iterable) {
		return of(iterable, false);
	}

	/**
	 * {@link Iterable}转换为{@link Stream}
	 *
	 * @param iterable 集合
	 * @param parallel 是否并行
	 * @param <T>      集合元素类型
	 * @return {@link Stream}
	 */
	public static <T> Stream<T> of(Iterable<T> iterable, boolean parallel) {
		Assert.notNull(iterable, "Iterable must be not null!");

		return iterable instanceof Collection ?
				parallel ? ((Collection<T>) iterable).parallelStream() : ((Collection<T>) iterable).stream() :
				StreamSupport.stream(iterable.spliterator(), parallel);
	}

	/**
	 * {@link Iterator} 转换为 {@link Stream}
	 * @param iterator 迭代器
	 * @param <T> 集合元素类型
	 * @return {@link Stream}
	 * @throws IllegalArgumentException 如果iterator为null，抛出该异常
	 */
	public static <T> Stream<T> of(Iterator<T> iterator) {
		return of(iterator, false);
	}

	/**
	 * {@link Iterator} 转换为 {@link Stream}
	 * @param iterator 迭代器
	 * @param parallel 是否并行
	 * @param <T> 集合元素类型
	 * @return {@link Stream}
	 * @throws IllegalArgumentException 如果iterator为null，抛出该异常
	 */
	public static <T> Stream<T> of(Iterator<T> iterator, boolean parallel) {
		Assert.notNull(iterator, "iterator must not be null!");
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), parallel);
	}

	/**
	 * 按行读取文件为{@link Stream}
	 *
	 * @param file 文件
	 * @return {@link Stream}
	 */
	public static Stream<String> of(File file) {
		return of(file, CharsetUtil.CHARSET_UTF_8);
	}

	/**
	 * 按行读取文件为{@link Stream}
	 *
	 * @param path 路径
	 * @return {@link Stream}
	 */
	public static Stream<String> of(Path path) {
		return of(path, CharsetUtil.CHARSET_UTF_8);
	}

	/**
	 * 按行读取文件为{@link Stream}
	 *
	 * @param file    文件
	 * @param charset 编码
	 * @return {@link Stream}
	 */
	public static Stream<String> of(File file, Charset charset) {
		Assert.notNull(file, "File must be not null!");
		return of(file.toPath(), charset);
	}

	/**
	 * 按行读取文件为{@link Stream}
	 *
	 * @param path    路径
	 * @param charset 编码
	 * @return {@link Stream}
	 */
	public static Stream<String> of(Path path, Charset charset) {
		try {
			return Files.lines(path, charset);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 通过函数创建Stream
	 *
	 * @param seed           初始值
	 * @param elementCreator 递进函数，每次调用此函数获取下一个值
	 * @param limit          限制个数
	 * @param <T>            创建元素类型
	 * @return {@link Stream}
	 */
	public static <T> Stream<T> of(T seed, UnaryOperator<T> elementCreator, int limit) {
		return Stream.iterate(seed, elementCreator).limit(limit);
	}

	/**
	 * 将Stream中所有元素以指定分隔符，合并为一个字符串，对象默认调用toString方法
	 *
	 * @param stream    {@link Stream}
	 * @param delimiter 分隔符
	 * @param <T>       元素类型
	 * @return 字符串
	 */
	public static <T> String join(Stream<T> stream, CharSequence delimiter) {
		return stream.collect(CollectorUtil.joining(delimiter));
	}

	/**
	 * 将Stream中所有元素以指定分隔符，合并为一个字符串
	 *
	 * @param stream       {@link Stream}
	 * @param delimiter    分隔符
	 * @param toStringFunc 元素转换为字符串的函数
	 * @param <T>          元素类型
	 * @return 字符串
	 */
	public static <T> String join(Stream<T> stream, CharSequence delimiter,
								  Function<T, ? extends CharSequence> toStringFunc) {
		return stream.collect(CollectorUtil.joining(delimiter, toStringFunc));
	}
}
