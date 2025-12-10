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
package io.github.future0923.debug.tools.idea.tool.action;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindow;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * 仿 IDEA 原生 "About" 样式的帮助弹窗
 *
 * @author caoayu
 */
public class HelpAction extends BaseToolAction {

    private static final String PLUGIN_ID = "io.github.future0923.DebugPower";
    private static final String OFFICIAL_URL = "https://debug-tools.cc/";

    public HelpAction() {
        getTemplatePresentation().setText(DebugToolsBundle.message("action.help.text"));
        getTemplatePresentation().setIcon(DebugToolsIcons.Help);
    }

    @Override
    protected void doActionPerformed(Project project, DebugToolsToolWindow toolWindow) {
        showAboutPopup(project);
    }

    private void showAboutPopup(Project project) {
        // 1. 获取基础信息
        PluginId pluginId = PluginId.getId(PLUGIN_ID);
        IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(pluginId);
        String version = (plugin != null) ? plugin.getVersion() : "Unknown";
        String pluginName = (plugin != null) ? plugin.getName() : DebugToolsBundle.message("plugin.name");
        String vendor = (plugin != null && plugin.getVendor() != null) ? plugin.getVendor() : DebugToolsBundle.message("plugin.vendor");

        // 2. 构建主容器 (BorderLayout)
        JPanel rootPanel = new JBPanel<>(new BorderLayout());
        rootPanel.setBackground(UIUtil.getPanelBackground());
        rootPanel.setPreferredSize(new Dimension(550, 300)); // 设定一个接近原生弹窗的大小

        // --- 中间内容区域 (包含左侧图标 + 右侧文字) ---
        JPanel centerPanel = new JBPanel<>(new GridBagLayout());
        centerPanel.setBackground(UIUtil.getPanelBackground());
        centerPanel.setBorder(JBUI.Borders.empty(30, 30, 20, 30)); // 整体内边距

        GridBagConstraints gbc = new GridBagConstraints();

        // [左侧] 图标
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 5; // 跨越多行
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = JBUI.insetsRight(25); // 图标和文字的间距

        // 获取并放大图标 (原生About图标通常很大，约64x64)
        Icon icon = DebugToolsIcons.DebugTools;
        // 如果你的图标太小，这里将其放大 4 倍 (16*4=64)
        Icon scaledIcon = IconUtil.scale(icon, null, 4.0f);
        centerPanel.add(new JBLabel(scaledIcon), gbc);

        // [右侧] 信息流
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = JBUI.emptyInsets();

        // 1. 插件名称 (大标题)
        JBLabel titleLabel = new JBLabel(pluginName);
        titleLabel.setFont(JBUI.Fonts.label(22).asBold()); // 22px 加粗
        titleLabel.setForeground(UIUtil.getLabelForeground());
        centerPanel.add(titleLabel, gbc);

        // 2. 版本号 (灰色)
        gbc.gridy++;
        gbc.insets = JBUI.insetsTop(5);
        JBLabel versionLabel = new JBLabel("Version " + version);
        versionLabel.setForeground(UIUtil.getContextHelpForeground());
        centerPanel.add(versionLabel, gbc);

        // 3. 描述/Vendor (正文)
        gbc.gridy++;
        gbc.insets = JBUI.insetsTop(20); // 与标题的大间距
        // 使用 HTML 允许换行
        JBLabel descLabel = new JBLabel("<html>Debug Tools for IntelliJ IDEA.<br>Developed by " + vendor + ".</html>");
        descLabel.setForeground(UIUtil.getLabelForeground());
        centerPanel.add(descLabel, gbc);

        // 4. 环境信息 (模拟截图中的 Runtime version)
        gbc.gridy++;
        gbc.insets = JBUI.insetsTop(10);
        String ideInfo = ApplicationInfo.getInstance().getFullApplicationName() +
                " (" + ApplicationInfo.getInstance().getBuild().asString() + ")";
        JBLabel ideLabel = new JBLabel(ideInfo);
        ideLabel.setForeground(UIUtil.getContextHelpForeground());
        ideLabel.setFont(JBUI.Fonts.smallFont());
        centerPanel.add(ideLabel, gbc);

        // 5. 链接
        gbc.gridy++;
        gbc.gridy++;
        gbc.insets = JBUI.insetsTop(20);

        // 【关键点1】使用 ActionLink 代替 HyperlinkLabel
        // ActionLink 就是纯文字链接，默认没有箭头，点击触发回调
        ActionLink link = new ActionLink(OFFICIAL_URL, e -> {
            BrowserUtil.browse(OFFICIAL_URL);
        });

        // 【关键点2】设置 fill 为 NONE，防止组件被拉伸占满整行
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST; // 确保靠左对齐

        centerPanel.add(link, gbc);

        // 6. 版权 (底部)
        gbc.gridy++;
        gbc.insets = JBUI.insetsTop(5);
        JBLabel copyrightLabel = new JBLabel("Copyright © 2024-2025 " + vendor);
        copyrightLabel.setForeground(UIUtil.getContextHelpForeground());
        copyrightLabel.setFont(JBUI.Fonts.smallFont());
        centerPanel.add(copyrightLabel, gbc);

        rootPanel.add(centerPanel, BorderLayout.CENTER);

        // --- 底部按钮区域 ---
        JPanel buttonPanel = new JBPanel<>(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(JBUI.Borders.empty(10, 20));

        // 定义 Popup 引用，以便在按钮事件中关闭它
        final JBPopup[] popupRef = new JBPopup[1];

        // "Copy and Close" 按钮 (蓝色默认样式)
        JButton copyButton = new JButton(DebugToolsBundle.message("global.param.panel.copy.close"));
        // 设置为默认按钮样式（蓝色）
        JRootPane rootPane = SwingUtilities.getRootPane(buttonPanel);
        if (rootPane != null) {
            rootPane.setDefaultButton(copyButton);
        }
        // 手动设置一些蓝色样式的属性 (可选，Idea 默认 LookAndFeel 会处理大部分)
        copyButton.addActionListener(e -> {
            copyInfoToClipboard(pluginName, version, vendor, ideInfo);
            if (popupRef[0] != null) popupRef[0].cancel();
        });

        // "Close" 按钮
        JButton closeButton = new JButton(DebugToolsBundle.message("global.param.panel.close"));
        closeButton.addActionListener(e -> {
            if (popupRef[0] != null) popupRef[0].cancel();
        });

        buttonPanel.add(copyButton);
        buttonPanel.add(closeButton);

        rootPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 3. 创建并显示
        popupRef[0] = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(rootPanel, copyButton) // 默认焦点在 copy 按钮
                .setMovable(true)
                .setFocusable(true)
                .setRequestFocus(true)
                .setCancelOnClickOutside(true)
                .setResizable(false)
                .setTitle("About " + pluginName) // 窗口标题栏
                .createPopup();

        popupRef[0].showCenteredInCurrentWindow(project);
    }

    /**
     * 复制信息到剪贴板
     */
    private void copyInfoToClipboard(String name, String version, String vendor, String ideInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ").append(version).append("\n");
        sb.append("Vendor: ").append(vendor).append("\n");
        sb.append("IDE: ").append(ideInfo).append("\n");
        sb.append("Website: ").append(OFFICIAL_URL).append("\n");

        CopyPasteManager.getInstance().setContents(new StringSelection(sb.toString()));
    }
}