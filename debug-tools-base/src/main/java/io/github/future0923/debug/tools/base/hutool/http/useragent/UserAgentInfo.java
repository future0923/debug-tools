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

import io.github.future0923.debug.tools.base.hutool.core.util.ReUtil;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * User-agent信息
 *
 * @author looly
 * @since 4.2.1
 */
public class UserAgentInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 未知类型
	 */
	public static final String NameUnknown = "Unknown";

	/** 信息名称 */
	private final String name;
	/** 信息匹配模式 */
	private final Pattern pattern;

	/**
	 * 构造
	 *
	 * @param name 名字
	 * @param regex 表达式
	 */
	public UserAgentInfo(String name, String regex) {
		this(name, (null == regex) ? null : Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
	}

	/**
	 * 构造
	 *
	 * @param name 名字
	 * @param pattern 匹配模式
	 */
	public UserAgentInfo(String name, Pattern pattern) {
		this.name = name;
		this.pattern = pattern;
	}

	/**
	 * 获取信息名称
	 *
	 * @return 信息名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获取匹配模式
	 *
	 * @return 匹配模式
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * 指定内容中是否包含匹配此信息的内容
	 *
	 * @param content User-Agent字符串
	 * @return 是否包含匹配此信息的内容
	 */
	public boolean isMatch(String content) {
		return ReUtil.contains(this.pattern, content);
	}

	/**
	 * 是否为Unknown
	 *
	 * @return 是否为Unknown
	 */
	public boolean isUnknown() {
		return NameUnknown.equals(this.name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final UserAgentInfo other = (UserAgentInfo) obj;
		if (name == null) {
			return other.name == null;
		} else return name.equals(other.name);
	}

	@Override
	public String toString() {
		return this.name;
	}
}
