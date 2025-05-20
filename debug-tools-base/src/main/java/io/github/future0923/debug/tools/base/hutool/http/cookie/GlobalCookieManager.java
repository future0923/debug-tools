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
package io.github.future0923.debug.tools.base.hutool.http.cookie;

import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.util.URLUtil;
import io.github.future0923.debug.tools.base.hutool.http.HttpConnection;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局Cookie管理器，只针对Hutool请求有效
 *
 * @author Looly
 * @since 4.5.15
 */
public class GlobalCookieManager {

	/** Cookie管理 */
	private static CookieManager cookieManager;
	static {
		cookieManager = new CookieManager(new ThreadLocalCookieStore(), CookiePolicy.ACCEPT_ALL);
	}

	/**
	 * 自定义{@link CookieManager}
	 *
	 * @param customCookieManager 自定义的{@link CookieManager}
	 */
	public static void setCookieManager(CookieManager customCookieManager) {
		cookieManager = customCookieManager;
	}

	/**
	 * 获取全局{@link CookieManager}
	 *
	 * @return {@link CookieManager}
	 */
	public static CookieManager getCookieManager() {
		return cookieManager;
	}

	/**
	 * 获取指定域名下所有Cookie信息
	 *
	 * @param conn HTTP连接
	 * @return Cookie信息列表
	 * @since 4.6.9
	 */
	public static List<HttpCookie> getCookies(HttpConnection conn){
		return cookieManager.getCookieStore().get(getURI(conn));
	}

	/**
	 * 将本地存储的Cookie信息附带到Http请求中，不覆盖用户定义好的Cookie
	 *
	 * @param conn {@link HttpConnection}
	 */
	public static void add(HttpConnection conn) {
		if(null == cookieManager) {
			// 全局Cookie管理器关闭
			return;
		}

		Map<String, List<String>> cookieHeader;
		try {
			cookieHeader = cookieManager.get(getURI(conn), new HashMap<>(0));
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}

		// 不覆盖模式回填Cookie头，这样用户定义的Cookie将优先
		conn.header(cookieHeader, false);
	}

	/**
	 * 存储响应的Cookie信息到本地
	 *
	 * @param conn {@link HttpConnection}
	 */
	public static void store(HttpConnection conn) {
		if(null == cookieManager) {
			// 全局Cookie管理器关闭
			return;
		}

		try {
			cookieManager.put(getURI(conn), conn.headers());
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * 获取连接的URL中URI信息
	 * @param conn HttpConnection
	 * @return URI
	 */
	private static URI getURI(HttpConnection conn){
		return URLUtil.toURI(conn.getUrl());
	}
}
