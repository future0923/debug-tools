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
