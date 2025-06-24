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
package io.github.future0923.debug.tools.base.hutool.core.compiler;

import javax.tools.DiagnosticCollector;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 诊断工具类
 *
 * @author looly
 * @since 5.5.2
 */
public class DiagnosticUtil {

	/**
	 * 获取{@link DiagnosticCollector}收集到的诊断信息，以文本返回
	 *
	 * @param collector {@link DiagnosticCollector}
	 * @return 诊断消息
	 */
	public static String getMessages(DiagnosticCollector<?> collector) {
		final List<?> diagnostics = collector.getDiagnostics();
		return diagnostics.stream().map(String::valueOf)
				.collect(Collectors.joining(System.lineSeparator()));
	}
}
