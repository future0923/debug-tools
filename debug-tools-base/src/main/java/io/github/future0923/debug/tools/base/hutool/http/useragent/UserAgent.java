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
package io.github.future0923.debug.tools.base.hutool.http.useragent;


import java.io.Serializable;

/**
 * User-Agent信息对象
 *
 * @author looly
 * @since 4.2.1
 */
public class UserAgent implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 是否为移动平台
	 */
	private boolean mobile;
	/**
	 * 浏览器类型
	 */
	private Browser browser;
	/**
	 * 浏览器版本
	 */
	private String version;

	/**
	 * 平台类型
	 */
	private Platform platform;

	/**
	 * 系统类型
	 */
	private OS os;
	/**
	 * 系统版本
	 */
	private String osVersion;

	/**
	 * 引擎类型
	 */
	private Engine engine;
	/**
	 * 引擎版本
	 */
	private String engineVersion;

	/**
	 * 是否为移动平台
	 *
	 * @return 是否为移动平台
	 */
	public boolean isMobile() {
		return mobile;
	}

	/**
	 * 设置是否为移动平台
	 *
	 * @param mobile 是否为移动平台
	 */
	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}

	/**
	 * 获取浏览器类型
	 *
	 * @return 浏览器类型
	 */
	public Browser getBrowser() {
		return browser;
	}

	/**
	 * 设置浏览器类型
	 *
	 * @param browser 浏览器类型
	 */
	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	/**
	 * 获取平台类型
	 *
	 * @return 平台类型
	 */
	public Platform getPlatform() {
		return platform;
	}

	/**
	 * 设置平台类型
	 *
	 * @param platform 平台类型
	 */
	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	/**
	 * 获取系统类型
	 *
	 * @return 系统类型
	 */
	public OS getOs() {
		return os;
	}

	/**
	 * 设置系统类型
	 *
	 * @param os 系统类型
	 */
	public void setOs(OS os) {
		this.os = os;
	}

	/**
	 * 获取系统版本
	 *
	 * @return 系统版本
	 * @since 5.7.4
	 */
	public String getOsVersion() {
		return this.osVersion;
	}

	/**
	 * 设置系统版本
	 *
	 * @param osVersion 系统版本
	 * @since 5.7.4
	 */
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	/**
	 * 获取引擎类型
	 *
	 * @return 引擎类型
	 */
	public Engine getEngine() {
		return engine;
	}

	/**
	 * 设置引擎类型
	 *
	 * @param engine 引擎类型
	 */
	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	/**
	 * 获取浏览器版本
	 *
	 * @return 浏览器版本
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * 设置浏览器版本
	 *
	 * @param version 浏览器版本
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * 获取引擎版本
	 *
	 * @return 引擎版本
	 */
	public String getEngineVersion() {
		return engineVersion;
	}

	/**
	 * 设置引擎版本
	 *
	 * @param engineVersion 引擎版本
	 */
	public void setEngineVersion(String engineVersion) {
		this.engineVersion = engineVersion;
	}

}
