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
package io.github.future0923.debug.tools.base.hutool.core.lang;

import java.io.Serializable;

/**
 * 枚举元素通用接口，在自定义枚举上实现此接口可以用于数据转换<br>
 * 数据库保存时建议保存 intVal()而非ordinal()防备需求变更<br>
 *
 * @param <E> Enum类型
 * @author nierjia
 * @since 5.4.2
 */
public interface EnumItem<E extends EnumItem<E>> extends Serializable {

	String name();

	/**
	 * 在中文语境下，多数时间枚举会配合一个中文说明
	 *
	 * @return enum名
	 */
	default String text() {
		return name();
	}

	int intVal();

	/**
	 * 获取所有枚举对象
	 *
	 * @return 枚举对象数组
	 */
	@SuppressWarnings("unchecked")
	default E[] items() {
		return (E[]) this.getClass().getEnumConstants();
	}

	/**
	 * 通过int类型值查找兄弟其他枚举
	 *
	 * @param intVal int值
	 * @return Enum
	 */
	default E fromInt(Integer intVal) {
		if (intVal == null) {
			return null;
		}
		E[] vs = items();
		for (E enumItem : vs) {
			if (enumItem.intVal() == intVal) {
				return enumItem;
			}
		}
		return null;
	}

	/**
	 * 通过String类型的值转换，根据实现可以用name/text
	 *
	 * @param strVal String值
	 * @return Enum
	 */
	default E fromStr(String strVal) {
		if (strVal == null) {
			return null;
		}
		E[] vs = items();
		for (E enumItem : vs) {
			if (strVal.equalsIgnoreCase(enumItem.name())) {
				return enumItem;
			}
		}
		return null;
	}


}

