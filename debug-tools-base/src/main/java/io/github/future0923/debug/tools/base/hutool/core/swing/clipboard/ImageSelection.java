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
package io.github.future0923.debug.tools.base.hutool.core.swing.clipboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.Serializable;

/**
 * 图片转换器，用于将图片对象转换为剪贴板支持的对象<br>
 * 此对象也用于将图像文件和{@link DataFlavor#imageFlavor} 元信息对应
 *
 * @author looly
 * @since 4.5.6
 */
public class ImageSelection implements Transferable, Serializable {
	private static final long serialVersionUID = 1L;

	private final Image image;

	/**
	 * 构造
	 *
	 * @param image 图片
	 */
	public ImageSelection(Image image) {
		this.image = image;
	}

	/**
	 * 获取元数据类型信息
	 *
	 * @return 元数据类型列表
	 */
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	/**
	 * 是否支持指定元数据类型
	 *
	 * @param flavor 元数据类型
	 * @return 是否支持
	 */
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.imageFlavor.equals(flavor);
	}

	/**
	 * 获取图片
	 *
	 * @param flavor 元数据类型
	 * @return 转换后的对象
	 */
	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (false == DataFlavor.imageFlavor.equals(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return image;
	}
}
