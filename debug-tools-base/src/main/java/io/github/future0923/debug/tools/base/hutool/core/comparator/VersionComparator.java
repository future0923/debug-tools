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
package io.github.future0923.debug.tools.base.hutool.core.comparator;

import io.github.future0923.debug.tools.base.hutool.core.lang.Version;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 版本比较器<br>
 * 比较两个版本的大小<br>
 * 排序时版本从小到大排序，即比较时小版本在前，大版本在后<br>
 * 支持如：1.3.20.8，6.82.20160101，8.5a/8.5c等版本形式<br>
 * 参考：java.lang.module.ModuleDescriptor.Version
 *
 * @author Looly
 * @since 4.0.2
 */
public class VersionComparator implements Comparator<String>, Serializable {
	private static final long serialVersionUID = 8083701245147495562L;

	/** 单例 */
	public static final VersionComparator INSTANCE = new VersionComparator();

	/**
	 * 默认构造
	 */
	public VersionComparator() {
	}

	// -----------------------------------------------------------------------------------------------------
	/**
	 * 比较两个版本<br>
	 * null版本排在最小：即：
	 * <pre>
	 * compare(null, "v1") &lt; 0
	 * compare("v1", "v1")  = 0
	 * compare(null, null)   = 0
	 * compare("v1", null) &gt; 0
	 * compare("1.0.0", "1.0.2") &lt; 0
	 * compare("1.0.2", "1.0.2a") &lt; 0
	 * compare("1.0.3", "1.0.2a") &gt; 0
	 * compare("1.13.0", "1.12.1c") &gt; 0
	 * compare("V0.0.20170102", "V0.0.20170101") &gt; 0
	 * </pre>
	 *
	 * @param version1 版本1
	 * @param version2 版本2
	 */
	@Override
	public int compare(String version1, String version2) {
		if(ObjectUtil.equal(version1, version2)) {
			return 0;
		}
		if (version1 == null && version2 == null) {
			return 0;
		} else if (version1 == null) {// null或""视为最小版本，排在前
			return -1;
		} else if (version2 == null) {
			return 1;
		}

		return CompareUtil.compare(Version.of(version1), Version.of(version2));
	}
}
