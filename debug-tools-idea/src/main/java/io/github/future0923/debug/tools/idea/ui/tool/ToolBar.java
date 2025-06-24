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
package io.github.future0923.debug.tools.idea.ui.tool;

import com.intellij.util.ui.JBDimension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * @author future0923
 */
public class ToolBar extends JToolBar {

    public ToolBar() {
        super();
        // 这行代码将组件的不透明性设置为 false。当一个组件设置为不透明时，它会完全覆盖其背景。通过将其设置为 false，组件将透明，即其内容可以透过组件的背景显示出来。这通常用于创建具有自定义绘制效果的组件，例如具有半透明背景的面板。
        this.setOpaque(false);
        // 这行代码将组件的“浮动”属性设置为 false。浮动工具栏通常允许用户将其拖动到应用程序窗口的边缘并停靠在那里。通过将浮动设置为 false，您可以禁用此功能，使工具栏不可移动。
        this.setFloatable(false);
    }

    public JButton genButton(String tip, Icon icon, Icon hoverIcon, Consumer<ActionEvent> consumer) {
        JButton button = new JButton();
        button.setToolTipText(tip);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.addActionListener(consumer::accept);
        button.setIcon(icon);
        button.setPreferredSize(new JBDimension(50, 30));
        button.addMouseListener(new MouseAdapter() {
            private final Color background = button.getBackground();
            @Override
            public void mouseEntered(MouseEvent e) {
                // 鼠标进入按钮时，提高亮度
                button.setIcon(hoverIcon);
                button.setBackground(background.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 鼠标离开按钮时，恢复原来的颜色
                button.setIcon(icon);
                button.setBackground(background);
            }
        });
        this.add(button);
        return button;
    }
}
