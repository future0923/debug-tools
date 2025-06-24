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
package io.github.future0923.debug.tools.base.hutool.core.swing.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;

/**
 * 剪贴板监听事件处理接口<br>
 * 用户通过实现此接口，实现监听剪贴板内容变化
 *
 * @author looly
 *@since 4.5.6
 */
public interface ClipboardListener {
	/**
	 * 剪贴板变动触发的事件方法<br>
	 * 在此事件中对剪贴板设置值无效，如若修改，需返回修改内容
	 *
	 * @param clipboard 剪贴板对象
	 * @param contents 内容
	 * @return 如果对剪贴板内容做修改，则返回修改的内容，{@code null}表示保留原内容
	 */
	Transferable onChange(Clipboard clipboard, Transferable contents);
}
