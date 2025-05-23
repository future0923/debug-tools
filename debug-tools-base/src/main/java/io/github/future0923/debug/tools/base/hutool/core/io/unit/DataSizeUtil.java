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
package io.github.future0923.debug.tools.base.hutool.core.io.unit;

import io.github.future0923.debug.tools.base.hutool.core.util.ArrayUtil;

import java.text.DecimalFormat;

/**
 * 数据大小工具类
 *
 * @author looly
 * @since 5.3.10
 */
public class DataSizeUtil {

	/**
	 * 解析数据大小字符串，转换为bytes大小
	 *
	 * @param text 数据大小字符串，类似于：12KB, 5MB等
	 * @return bytes大小
	 */
	public static long parse(String text) {
		return DataSize.parse(text).toBytes();
	}

	/**
	 * 可读的文件大小<br>
	 * 参考 http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
	 *
	 * @param size Long类型大小
	 * @return 大小
	 */
	public static String format(long size) {
		if (size <= 0) {
			return "0";
		}
		int digitGroups = Math.min(DataUnit.UNIT_NAMES.length-1, (int) (Math.log10(size) / Math.log10(1024)));
		return new DecimalFormat("#,##0.##")
				.format(size / Math.pow(1024, digitGroups)) + " " + DataUnit.UNIT_NAMES[digitGroups];
	}

	/**
	 * 根据单位，将文件大小转换为对应单位的大小
	 *
	 * @param size 文件大小
	 * @param fileDataUnit 单位
	 * @return 大小
	 * @since 5.8.34
	 */
	public static String format(Long size, DataUnit fileDataUnit){
		if (size <= 0) {
			return "0";
		}
		int digitGroups = ArrayUtil.indexOf(DataUnit.UNIT_NAMES,fileDataUnit.getSuffix());
		return new DecimalFormat("##0.##").format(size / Math.pow(1024, digitGroups)) + " " + DataUnit.UNIT_NAMES[digitGroups];
	}
}
