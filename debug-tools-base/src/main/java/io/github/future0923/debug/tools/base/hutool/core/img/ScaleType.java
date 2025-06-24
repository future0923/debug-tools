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
package io.github.future0923.debug.tools.base.hutool.core.img;

import java.awt.*;

/**
 * 图片缩略算法类型
 *
 * @author looly
 * @since 4.5.8
 */
public enum ScaleType {

	/** 默认 */
	DEFAULT(Image.SCALE_DEFAULT),
	/** 快速 */
	FAST(Image.SCALE_FAST),
	/** 平滑 */
	SMOOTH(Image.SCALE_SMOOTH),
	/** 使用 ReplicateScaleFilter 类中包含的图像缩放算法 */
	REPLICATE(Image.SCALE_REPLICATE),
	/** Area Averaging算法 */
	AREA_AVERAGING(Image.SCALE_AREA_AVERAGING);

	/**
	 * 构造
	 *
	 * @param value 缩放方式
	 * @see Image#SCALE_DEFAULT
	 * @see Image#SCALE_FAST
	 * @see Image#SCALE_SMOOTH
	 * @see Image#SCALE_REPLICATE
	 * @see Image#SCALE_AREA_AVERAGING
	 */
	ScaleType(int value) {
		this.value = value;
	}

	private final int value;

	public int getValue() {
		return this.value;
	}
}
