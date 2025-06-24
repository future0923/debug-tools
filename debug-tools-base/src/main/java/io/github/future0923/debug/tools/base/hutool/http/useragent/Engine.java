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
package io.github.future0923.debug.tools.base.hutool.http.useragent;

import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ReUtil;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 引擎对象
 *
 * @author looly
 * @since 4.2.1
 */
public class Engine extends UserAgentInfo {
	private static final long serialVersionUID = 1L;

	/** 未知 */
	public static final Engine Unknown = new Engine(NameUnknown, null);

	/**
	 * 支持的引擎类型
	 */
	public static final List<Engine> engines = CollUtil.newArrayList(
			new Engine("Trident", "trident"),
			new Engine("Webkit", "webkit"),
			new Engine("Chrome", "chrome"),
			new Engine("Opera", "opera"),
			new Engine("Presto", "presto"),
			new Engine("Gecko", "gecko"),
			new Engine("KHTML", "khtml"),
			new Engine("Konqueror", "konqueror"),
			new Engine("MIDP", "MIDP")
	);

	private final Pattern versionPattern;

	/**
	 * 构造
	 *
	 * @param name 引擎名称
	 * @param regex 关键字或表达式
	 */
	public Engine(String name, String regex) {
		super(name, regex);
		this.versionPattern = Pattern.compile(name + "[/\\- ]([\\d\\w.\\-]+)", Pattern.CASE_INSENSITIVE);
	}

	/**
	 * 获取引擎版本
	 *
	 * @param userAgentString User-Agent字符串
	 * @return 版本
	 * @since 5.7.4
	 */
	public String getVersion(String userAgentString) {
		if(isUnknown()){
			return null;
		}
		return ReUtil.getGroup1(this.versionPattern, userAgentString);
	}
}
