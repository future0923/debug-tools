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
package io.github.future0923.debug.tools.base.hutool.http.cookie;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * 线程隔离的Cookie存储。多线程环境下Cookie隔离使用，防止Cookie覆盖<br>
 * 
 * 见：https://stackoverflow.com/questions/16305486/cookiemanager-for-multiple-threads
 * 
 * @author looly
 * @since 4.1.18
 */
public class ThreadLocalCookieStore implements CookieStore {

	private final static ThreadLocal<CookieStore> STORES = new ThreadLocal<CookieStore>() {
		@Override
		protected synchronized CookieStore initialValue() {
			/* InMemoryCookieStore */
			return (new CookieManager()).getCookieStore();
		}
	};

	/**
	 * 获取本线程下的CookieStore
	 * 
	 * @return CookieStore
	 */
	public CookieStore getCookieStore() {
		return STORES.get();
	}

	/**
	 * 移除当前线程的Cookie
	 * 
	 * @return this
	 */
	public ThreadLocalCookieStore removeCurrent() {
		STORES.remove();
		return this;
	}

	@Override
	public void add(URI uri, HttpCookie cookie) {
		getCookieStore().add(uri, cookie);
	}

	@Override
	public List<HttpCookie> get(URI uri) {
		return getCookieStore().get(uri);
	}

	@Override
	public List<HttpCookie> getCookies() {
		return getCookieStore().getCookies();
	}

	@Override
	public List<URI> getURIs() {
		return getCookieStore().getURIs();
	}

	@Override
	public boolean remove(URI uri, HttpCookie cookie) {
		return getCookieStore().remove(uri, cookie);
	}

	@Override
	public boolean removeAll() {
		return getCookieStore().removeAll();
	}
}
