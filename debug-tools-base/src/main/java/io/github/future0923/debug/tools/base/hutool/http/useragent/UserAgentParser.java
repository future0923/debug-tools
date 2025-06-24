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

import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

/**
 * User-Agent解析器
 *
 * @author looly
 * @since 4.2.1
 */
public class UserAgentParser {

	/**
	 * 解析User-Agent
	 *
	 * @param userAgentString User-Agent字符串
	 * @return {@link UserAgent}
	 */
	public static UserAgent parse(String userAgentString) {
		if(StrUtil.isBlank(userAgentString)){
			return null;
		}
		final UserAgent userAgent = new UserAgent();

		// 浏览器
		final Browser browser = parseBrowser(userAgentString);
		userAgent.setBrowser(browser);
		userAgent.setVersion(browser.getVersion(userAgentString));

		// 浏览器引擎
		final Engine engine = parseEngine(userAgentString);
		userAgent.setEngine(engine);
		userAgent.setEngineVersion(engine.getVersion(userAgentString));

		// 操作系统
		final OS os = parseOS(userAgentString);
		userAgent.setOs(os);
		userAgent.setOsVersion(os.getVersion(userAgentString));

		// 平台
		final Platform platform = parsePlatform(userAgentString);
		userAgent.setPlatform(platform);

		// issue#IA74K2 MACOS下的微信不属于移动平台
		if(platform.isMobile() || browser.isMobile()){
			if(false == os.isMacOS()){
				userAgent.setMobile(true);
			}
		}


		return userAgent;
	}

	/**
	 * 解析浏览器类型
	 *
	 * @param userAgentString User-Agent字符串
	 * @return 浏览器类型
	 */
	private static Browser parseBrowser(String userAgentString) {
		for (Browser browser : Browser.browers) {
			if (browser.isMatch(userAgentString)) {
				return browser;
			}
		}
		return Browser.Unknown;
	}

	/**
	 * 解析引擎类型
	 *
	 * @param userAgentString User-Agent字符串
	 * @return 引擎类型
	 */
	private static Engine parseEngine(String userAgentString) {
		for (Engine engine : Engine.engines) {
			if (engine.isMatch(userAgentString)) {
				return engine;
			}
		}
		return Engine.Unknown;
	}

	/**
	 * 解析系统类型
	 *
	 * @param userAgentString User-Agent字符串
	 * @return 系统类型
	 */
	private static OS parseOS(String userAgentString) {
		for (OS os : OS.oses) {
			if (os.isMatch(userAgentString)) {
				return os;
			}
		}
		return OS.Unknown;
	}

	/**
	 * 解析平台类型
	 *
	 * @param userAgentString User-Agent字符串
	 * @return 平台类型
	 */
	private static Platform parsePlatform(String userAgentString) {
		for (Platform platform : Platform.platforms) {
			if (platform.isMatch(userAgentString)) {
				return platform;
			}
		}
		return Platform.Unknown;
	}
}
