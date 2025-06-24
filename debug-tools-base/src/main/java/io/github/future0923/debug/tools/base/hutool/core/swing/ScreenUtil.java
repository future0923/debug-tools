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
package io.github.future0923.debug.tools.base.hutool.core.swing;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 屏幕相关（当前显示设置）工具类
 *
 * @author looly
 * @since 4.1.14
 */
public class ScreenUtil {
	public static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

	/**
	 * 获取屏幕宽度
	 *
	 * @return 屏幕宽度
	 */
	public static int getWidth() {
		return (int) dimension.getWidth();
	}

	/**
	 * 获取屏幕高度
	 *
	 * @return 屏幕高度
	 */
	public static int getHeight() {
		return (int) dimension.getHeight();
	}

	/**
	 * 获取屏幕的矩形
	 * @return 屏幕的矩形
	 */
	public static Rectangle getRectangle() {
		return new Rectangle(getWidth(), getHeight());
	}

	//-------------------------------------------------------------------------------------------- 截屏
	/**
	 * 截取全屏
	 *
	 * @return 截屏的图片
	 * @see RobotUtil#captureScreen()
	 */
	public static BufferedImage captureScreen() {
		return RobotUtil.captureScreen();
	}

	/**
	 * 截取全屏到文件
	 *
	 * @param outFile 写出到的文件
	 * @return 写出到的文件
	 * @see RobotUtil#captureScreen(File)
	 */
	public static File captureScreen(File outFile) {
		return RobotUtil.captureScreen(outFile);
	}

	/**
	 * 截屏
	 *
	 * @param screenRect 截屏的矩形区域
	 * @return 截屏的图片
	 * @see RobotUtil#captureScreen(Rectangle)
	 */
	public static BufferedImage captureScreen(Rectangle screenRect) {
		return RobotUtil.captureScreen(screenRect);
	}

	/**
	 * 截屏
	 *
	 * @param screenRect 截屏的矩形区域
	 * @param outFile 写出到的文件
	 * @return 写出到的文件
	 * @see RobotUtil#captureScreen(Rectangle, File)
	 */
	public static File captureScreen(Rectangle screenRect, File outFile) {
		return RobotUtil.captureScreen(screenRect, outFile);
	}
}
