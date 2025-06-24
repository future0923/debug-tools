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
package io.github.future0923.debug.tools.base.hutool.core.lang.ansi;

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.util.Objects;

/**
 * ANSI 颜色包装类
 *
 * @author TomXin
 * @since 5.8.6
 */
public class AnsiColorWrapper {

	private final int code;

	private final AnsiColors.BitDepth bitDepth;

	/**
	 * 创建指定位深度的 {@code AnsiColorWrapper} 实例
	 *
	 * @param code     颜色编码，位深度为4bit时，code取值范围[30~37]，[90~97]。位深度为8bit时，code取值范围[0~255]
	 * @param bitDepth 位深度
	 */
	public AnsiColorWrapper(int code, AnsiColors.BitDepth bitDepth) {
		if (bitDepth == AnsiColors.BitDepth.FOUR) {
			Assert.isTrue((30 <= code && code <= 37) || (90 <= code && code <= 97), "The value of 4 bit color only supported [30~37],[90~97].");
		}
		Assert.isTrue((0 <= code && code <= 255), "The value of 8 bit color only supported [0~255].");
		this.code = code;
		this.bitDepth = bitDepth;
	}

	/**
	 * 转换为 {@link AnsiElement} 实例
	 *
	 * @param foreOrBack 区分前景还是背景
	 * @return {@link AnsiElement} 实例
	 */
	public AnsiElement toAnsiElement(ForeOrBack foreOrBack) {
		if (bitDepth == AnsiColors.BitDepth.FOUR) {
			if (foreOrBack == ForeOrBack.FORE) {
				for (AnsiColor item : AnsiColor.values()) {
					if (item.getCode() == this.code) {
						return item;
					}
				}
				throw new IllegalArgumentException(StrUtil.format("No matched AnsiColor instance,code={}", this.code));
			}
			for (AnsiBackground item : AnsiBackground.values()) {
				if (item.getCode() == this.code + 10) {
					return item;
				}
			}
			throw new IllegalArgumentException(StrUtil.format("No matched AnsiBackground instance,code={}", this.code));
		}
		if (foreOrBack == ForeOrBack.FORE) {
			return Ansi8BitColor.foreground(this.code);
		}
		return Ansi8BitColor.background(this.code);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AnsiColorWrapper that = (AnsiColorWrapper) o;
		return this.code == that.code && this.bitDepth == that.bitDepth;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.code, this.bitDepth);
	}
}
