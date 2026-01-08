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
package io.github.future0923.debug.tools.idea.ui.main;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.tools.idea.file.DebugConfSyntaxHighlighter;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.ui.combobox.IgnoreStaticFieldComboBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

/**
 * 配置文件保存对话框
 *
 * @author future0923
 */
public class ConfDialogWrapper extends DialogWrapper {

    private final Project project;

    private final String initName;

    private final IgnoreStaticFieldComboBox comboBox;

    private final JBTextField nameTextField;

    private EditorTextField editorField;

    public ConfDialogWrapper(@NotNull Project project, IgnoreStaticFieldComboBox comboBox, @Nullable String initName) {
        super(project, true, IdeModalityType.MODELESS);
        this.project = project;
        this.initName = initName;
        this.comboBox = comboBox;
        nameTextField = new JBTextField(initName, 20);
        setTitle(DebugToolsBundle.message("action.hotswap.ignore.static.field.conf"));
        setOKButtonText(DebugToolsBundle.message("action.save"));
        setCancelButtonText(DebugToolsBundle.message("action.cancel"));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        String content = "";
        if (StrUtil.isNotBlank(initName)) {
            String filePath = settingState.getIgnoreStaticFieldPathMap().get(initName);
            if (StrUtil.isNotBlank(filePath) && FileUtil.exist(filePath)) {
                content = FileUtil.readUtf8String(filePath);
            }
        }
        editorField = new EditorTextField(content, project, PlainTextFileType.INSTANCE) {
            @Override
            protected @NotNull EditorEx createEditor() {
                final EditorEx editor = super.createEditor();
                editor.setViewer(false);
                editor.setHorizontalScrollbarVisible(true);
                editor.setVerticalScrollbarVisible(true);
                // 关闭软换行(当一行太长超出可视区域时，在不插入真正换行符的前提下，按窗口宽度把这一行“视觉上”折到下一行显示。)
                editor.getSettings().setUseSoftWraps(false);
                editor.getSettings().setLineCursorWidth(EditorUtil.getDefaultCaretWidth());
                editor.getColorsScheme().setEditorFontName(getFont().getFontName());
                editor.getColorsScheme().setEditorFontSize(getFont().getSize());
                editor.getContentComponent().setBorder(new CompoundBorder(editor.getContentComponent().getBorder(), JBUI.Borders.emptyLeft(2)));
                editor.getSettings().setLineNumbersShown(true);
                editor.getSettings().setFoldingOutlineShown(true);
                editor.setBackgroundColor(JBColor.PanelBackground);
                editor.setHighlighter(
                        new LexerEditorHighlighter(
                                new DebugConfSyntaxHighlighter(),
                                EditorColorsManager
                                        .getInstance()
                                        .getGlobalScheme()
                        )
                );
                return editor;
            }
        };
        editorField.setPreferredSize(JBUI.size(900, 600));
        editorField.setOneLineMode(false);
        editorField.addNotify();

        // 5) 常用操作按钮
        JPanel toolbar = initToolBar();
        JBPanel<?> root = new JBPanel<>(new BorderLayout());
        root.add(toolbar, BorderLayout.NORTH);
        root.add(editorField, BorderLayout.CENTER);
        root.setPreferredSize(JBUI.size(900, 640));
        return root;
    }

    private @NotNull JPanel initToolBar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        namePanel.add(new JBLabel("Name"));
        namePanel.add(nameTextField); // 指定列数更稳一点
        toolbar.add(namePanel);
        return toolbar;
    }

    /**
     * 取最终完整文本
     */
    public String getFullText() {
        return editorField != null ? editorField.getText() : "";
    }

    @Override
    protected void doOKAction() {
        String name = nameTextField.getText();
        if (StrUtil.isBlank(name)) {
            Messages.showErrorDialog(DebugToolsBundle.message("method.around.name.error"), DebugToolsBundle.message("common.error"));
            return;
        }
        WriteCommandAction.runWriteCommandAction(project, () -> {
            String filePath = project.getBasePath() + IdeaPluginProjectConstants.IGNORE_STATIC_FIELD_DIR + name + ".conf";
            FileUtil.writeUtf8String(getFullText(), filePath);
            DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
            settingState.getIgnoreStaticFieldPathMap().put(name, filePath);
            if (!StrUtil.equals(initName, name)) {
                FileUtil.del(project.getBasePath() + IdeaPluginProjectConstants.IGNORE_STATIC_FIELD_DIR + initName + ".conf");
                comboBox.refresh();
                comboBox.setSelected(name);
            }
            if (StrUtil.equals(settingState.getIgnoreStaticFieldConfName(), filePath)) {
                settingState.reloadIgnoreStaticFieldByPath(project);
            }
        });
        super.doOKAction();
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }
}
