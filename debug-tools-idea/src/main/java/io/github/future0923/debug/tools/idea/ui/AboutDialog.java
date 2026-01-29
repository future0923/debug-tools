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
package io.github.future0923.debug.tools.idea.ui;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * About dialog for DebugTools plugin
 *
 * @author caoayu
 */
public class AboutDialog extends DialogWrapper {

    private static final String GITHUB_URL = "https://github.com/future0923/debug-tools";
    private static final String DOCS_URL = "https://debug-tools.cc";
    private static final String ABOUT_HTML_PATH = "/template/about-content.html";
    private static final String BUILD_INFO_PROPERTIES = "/build-info.properties";
    private static final String MARKETPLACE_URL = "https://plugins.jetbrains.com/plugin/24463-debugtools/reviews";

    private final IdeaPluginDescriptor pluginDesc;
    private final ApplicationInfo appInfo;
    private final Properties buildProps = new Properties();

    private String issueUrl;
    private String buildDateCache;
    private String ideVersionCache;
    private String ideNameCache;
    private String buildNumberCache;

    public AboutDialog(@Nullable Project project) {
        super(project, true);
        this.pluginDesc = PluginManagerCore.getPlugin(PluginId.findId(ProjectConstants.PLUGIN_ID));
        this.appInfo = ApplicationInfo.getInstance();

        // 加载构建信息
        try (InputStream is = AboutDialog.class.getResourceAsStream(BUILD_INFO_PROPERTIES)) {
            buildProps.load(is);
        } catch (IOException e) {
            // 静默处理，buildProps保持空
        }

        setTitle(DebugToolsBundle.message("about.dialog.title"));
        setResizable(false);
        init();
    }

    @Override
    protected void init() {
        super.init();
        setSize(550, 400);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        // Create main panel with modern design
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBorder(JBUI.Borders.empty(5, 25));
        mainPanel.setBackground(JBColor.WHITE);

        // Header panel with icon and title
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBorder(JBUI.Borders.emptyBottom(5));
        headerPanel.setBackground(JBColor.WHITE);

        // Plugin icon with shadow effect
        JLabel iconLabel = new JLabel(DebugToolsIcons.DebugToolsMax);
        iconLabel.setPreferredSize(new Dimension(80, 80));
        headerPanel.add(iconLabel, BorderLayout.WEST);

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout(0, 8));
        titlePanel.setBackground(JBColor.WHITE);

        // Plugin name with modern font
        JTextPane nameLabel = new JTextPane();
        nameLabel.setText(DebugToolsBundle.message("plugin.name"));
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 24f));
        nameLabel.setBackground(JBColor.WHITE);
        nameLabel.setEditable(false);
        nameLabel.setOpaque(false);
        titlePanel.add(nameLabel, BorderLayout.NORTH);

        // Plugin version with gradient effect
        JTextPane versionLabel = new JTextPane();
        versionLabel.setText("Version: " + getVersionInfo());
        versionLabel.setFont(versionLabel.getFont().deriveFont(Font.PLAIN, 16f));
        versionLabel.setBackground(JBColor.WHITE);
        versionLabel.setEditable(false);
        versionLabel.setOpaque(false);
        titlePanel.add(versionLabel, BorderLayout.CENTER);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // HTML content panel
        JEditorPane htmlPane = new JEditorPane();
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);
        htmlPane.setOpaque(false);
        htmlPane.setBorder(JBUI.Borders.empty(10, 20));

        // Load and process HTML template
        try {
            String htmlContent = loadAboutHtml();
            htmlPane.setText(htmlContent);
        } catch (IOException e) {
            htmlPane.setText("<html><body>Failed to load about content</body></html>");
        }

        // Add hyperlink listener
        htmlPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(URI.create(e.getURL().toString()));
                } catch (IOException ex) {
                    // Copy to clipboard if browser open fails
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(e.getURL().toString()), null);
                }
            }
        });

        mainPanel.add(htmlPane, BorderLayout.CENTER);
        return mainPanel;
    }


    private String getVendorEmail() {
        if (pluginDesc != null && pluginDesc.getVendorEmail() != null) {
            return pluginDesc.getVendorEmail();
        }
        return "future94@qq.com";
    }


    private String getVersionInfo() {
        if (pluginDesc == null) {
            return ProjectConstants.VERSION;
        } else {
            return pluginDesc.getVersion();
        }
    }


    private String getInstallDate() {
        if (pluginDesc == null) return "Unknown";
        try {
            // 获取插件在磁盘上的路径
            Path pluginPath = pluginDesc.getPluginPath();
            // 读取文件属性
            BasicFileAttributes attrs = Files.readAttributes(pluginPath, BasicFileAttributes.class);
            // 获取创建时间 (Windows上通常是创建时间，Linux上可能是最后修改时间)
            FileTime createTime = attrs.creationTime();

            // 格式化
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault())
                    .format(createTime.toInstant());
        } catch (IOException e) {
            return "Unknown";
        }
    }

    private String getRuntimeInfo() {
        return System.getProperty("java.version") + " " + System.getProperty("os.arch");
    }

    private String getVmInfo() {
        return System.getProperty("java.vm.name") + " by " + System.getProperty("java.vm.vendor");
    }

    private String getOsInfo() {
        return SystemInfo.getOsNameAndVersion();
    }

    private String getBuildNumber() {
        if (buildNumberCache == null) {
            buildNumberCache = appInfo.getBuild().asString();
        }
        return buildNumberCache;
    }

    private String getIdeName() {
        if (ideNameCache == null) {
            ideNameCache = appInfo.getVersionName();
        }
        return ideNameCache;
    }

    private String getIdeVersion() {
        if (ideVersionCache == null) {
            // IDE版本应该是IntelliJ IDEA的版本信息，而不是操作系统信息
            ideVersionCache = appInfo.getFullVersion();
        }
        return ideVersionCache;
    }

    private String getIssueUrl() {
        if (issueUrl == null) {
            String issueBody = URLEncoder.encode("## Environment " + getIdeInfo() + " " + getVersionInfo(), StandardCharsets.UTF_8);
            issueUrl = "https://github.com/future0923/debug-tools/issues/new?body=" + issueBody;
        }
        return issueUrl;
    }

    private String getBuildDate() {
        if (buildDateCache == null) {
            buildDateCache = buildProps.getProperty("build.date", "Unknown");
        }
        return buildDateCache;
    }

    private String loadAboutHtml() throws IOException {
        InputStream is = getClass().getResourceAsStream(ABOUT_HTML_PATH);
        if (is == null) {
            throw new IOException("About HTML file not found");
        }
        String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        // 1. 先替换硬编码的变量 (版本、URL等)
        html = html.replace("${version}", getVersionInfo())
                .replace("${githubUrl}", GITHUB_URL)
                .replace("${docsUrl}", DOCS_URL)
                .replace("${marketplaceUrl}", MARKETPLACE_URL)
                .replace("${issueUrl}", getIssueUrl())
                .replace("${installDate}", getInstallDate())
                .replace("${runtimeVersion}", getRuntimeInfo())
                .replace("${vmInfo}", getVmInfo())
                .replace("${osInfo}", getOsInfo())
                .replace("${ideName}", getIdeName())
                .replace("${ideVersion}", getIdeVersion())
                .replace("${buildNumber}", getBuildNumber())
                .replace("${buildDate}", getBuildDate());

        // 2. 动态替换所有剩余的 ${key} 为多语言内容
        // 正则解释：匹配 ${ 后面的 字母、数字、点、下划线、横线，直到 }
        Pattern pattern = Pattern.compile("\\$\\{([a-zA-Z0-9_.-]+)\\}");
        Matcher matcher = pattern.matcher(html);

        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1); // 获取括号里的 key，例如 about.html.description
            String replacement;

            // 尝试从 Bundle 获取翻译
            try {
                replacement = DebugToolsBundle.message(key);
            } catch (Exception e) {
                // 如果 key 不存在，保留原样或者设为空，避免报错
                // 这里保留原样方便排查： ${missing.key}
                replacement = "${" + key + "}";
            }

            // 处理 replacement 中的特殊字符（如 $），否则 appendReplacement 会报错
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String getIdeInfo() {
        ApplicationNamesInfo appInfo = ApplicationNamesInfo.getInstance();
        return appInfo.getFullProductName();
    }

    @Override
    protected Action[] createActions() {
        // 只保留关闭按钮，HTML内容已支持选中复制
        return new Action[]{
                new CancelActionWrapper()
        };
    }

    /**
     * Custom Cancel action with modern styling
     */
    private class CancelActionWrapper extends DialogWrapper.DialogWrapperAction {
        protected CancelActionWrapper() {
            super(DebugToolsBundle.message("action.cancel"));
            putValue(Action.MNEMONIC_KEY, (int) 'C');
        }

        @Override
        protected void doAction(ActionEvent e) {
            close(DialogWrapper.CLOSE_EXIT_CODE);
        }
    }
}