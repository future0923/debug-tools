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

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.testFramework.LightVirtualFile;
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
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.ui.combobox.MethodAroundComboBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

/**
 * @author future0923
 */
public class JavaEditorDialogWrapper extends DialogWrapper {

    private final Project project;

    private final String initName;

    private final MethodAroundComboBox methodAroundComboBox;

    private final JBTextField nameTextField;

    private PsiJavaFile psiJavaFile;

    private Document document;

    private EditorTextField editorField;

    private static final String initTemplate =
            """
                    package io.github.future0923.debug.tools.base.around;
                    
                    import java.util.List;
                    import java.util.Map;
                    
                    public class RunMethodAround {
                    
                        public void onBefore(Map<String, String> headers, String xxlJobParam, String className, String methodName, List<String> methodParameterTypes, Object[] methodParameters) {
                    
                        }
                    
                        public void onAfter(Map<String, String> headers, String xxlJobParam, String className, String methodName, List<String> methodParameterTypes, Object[] methodParameters, Object result) {
                    
                        }
                    
                        public void onException(Map<String, String> headers, String xxlJobParam, String className, String methodName, List<String> methodParameterTypes, Object[] methodParameters, Throwable throwable) {
                    
                        }

                        public void onFinally(Map<String, String> headers, String xxlJobParam, String className, String methodName, List<String> methodParameterTypes, Object[] methodParameters, Object result, Throwable throwable) {
                    
                        }
                    }
                    """;

    public JavaEditorDialogWrapper(@NotNull Project project, MethodAroundComboBox methodAroundComboBox, @Nullable String initName) {
        super(project, true, IdeModalityType.MODELESS);
        this.project = project;
        this.initName = initName;
        this.methodAroundComboBox = methodAroundComboBox;
        nameTextField = new JBTextField(initName, 20);
        setTitle(DebugToolsBundle.message("method.around"));
        setOKButtonText(DebugToolsBundle.message("action.save"));
        setCancelButtonText(DebugToolsBundle.message("action.cancel"));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        String content = initTemplate;
        if (StrUtil.isNotBlank(initName)) {
            String filePath = settingState.getMethodAroundMap().get(initName);
            if (StrUtil.isNotBlank(filePath) && FileUtil.exist(filePath)) {
                content = FileUtil.readUtf8String(filePath);
            }
        }
        LightVirtualFile vFile = new LightVirtualFile("RunMethodAround.java", JavaFileType.INSTANCE, content);
        vFile.setWritable(true);
        // 2) 绑定 Document + PSI
        FileDocumentManager fdm = FileDocumentManager.getInstance();
        document = fdm.getDocument(vFile);
        if (document == null) {
            document = EditorFactory.getInstance().createDocument(initTemplate);
        }
        psiJavaFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(vFile);
        if (psiJavaFile == null) {
            psiJavaFile = (PsiJavaFile) PsiFileFactory.getInstance(project)
                    .createFileFromText("RunMethodAround.java", JavaFileType.INSTANCE, initTemplate);
        }
        PsiDocumentManager.getInstance(project).commitDocument(document);
        editorField = new EditorTextField(document, project, JavaFileType.INSTANCE, false, true) {
            @Override
            protected @NotNull EditorEx createEditor() {
                final EditorEx editor = super.createEditor();
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
                return editor;
            }
        };
        editorField.setNewDocumentAndFileType(JavaFileType.INSTANCE, document);
        editorField.setPreferredSize(JBUI.size(900, 600));
        editorField.setOneLineMode(false);
        editorField.addNotify(); // 确保 editor 初始化
        EditorEx ex = (EditorEx) editorField.getEditor();
        registerNavActions(ex);

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
        JButton optimizeImportsBtn = new JButton(DebugToolsBundle.message("method.around.optimize"));
        optimizeImportsBtn.addActionListener(e -> runWrite(() ->
                JavaCodeStyleManager.getInstance(project).optimizeImports(psiJavaFile)));

        JButton reformatBtn = new JButton(DebugToolsBundle.message("method.around.format"));
        reformatBtn.addActionListener(e -> runWrite(() ->
                CodeStyleManager.getInstance(project).reformat(psiJavaFile)));

        JButton shortenRefsBtn = new JButton(DebugToolsBundle.message("method.around.short.class"));
        shortenRefsBtn.addActionListener(e -> runWrite(() ->
                JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiJavaFile)));

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        namePanel.add(new JBLabel(DebugToolsBundle.message("method.around.name")));
        namePanel.add(nameTextField); // 指定列数更稳一点
        toolbar.add(namePanel);
        toolbar.add(optimizeImportsBtn);
        toolbar.add(reformatBtn);
        toolbar.add(shortenRefsBtn);
        return toolbar;
    }

    private void runWrite(Runnable r) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiDocumentManager.getInstance(project).commitDocument(document);
            r.run();
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
        });
    }

    /**
     * 取最终完整 .java 文本（含 package/import/class）
     */
    public String getFullJavaText() {
        return editorField != null ? editorField.getText() : "";
    }

    // 1) 让默认快捷键生效（Ctrl/Cmd+B、Ctrl/Cmd+Alt+B、Ctrl/Cmd+Shift+B、Alt+F7 等）
    private static void registerNavActions(EditorEx editor) {
        ActionManager am = ActionManager.getInstance();
        register(editor, IdeActions.ACTION_GOTO_DECLARATION, am);
        register(editor, IdeActions.ACTION_GOTO_IMPLEMENTATION, am);
        register(editor, IdeActions.ACTION_GOTO_TYPE_DECLARATION, am);
        register(editor, IdeActions.ACTION_FIND_USAGES, am); // Alt+F7 / Show Usages
    }

    private static void register(EditorEx editor, String actionId, ActionManager am) {
        AnAction action = am.getAction(actionId);
        if (action != null) {
            action.registerCustomShortcutSet(action.getShortcutSet(), editor.getContentComponent());
        }
    }

    @Override
    protected void doOKAction() {
        String name = nameTextField.getText();
        if (StrUtil.isBlank(name)) {
            Messages.showErrorDialog(DebugToolsBundle.message("method.around.name.error"), DebugToolsBundle.message("common.error"));
            return;
        }
        WriteCommandAction.runWriteCommandAction(project, () -> {
            String filePath = project.getBasePath() + IdeaPluginProjectConstants.METHOD_AROUND_DIR + name + ".java";
            FileUtil.writeUtf8String(getFullJavaText(), filePath);
            DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
            settingState.getMethodAroundMap().put(name, filePath);
            if (!StrUtil.equals(initName, name)) {
                FileUtil.del(project.getBasePath() + IdeaPluginProjectConstants.METHOD_AROUND_DIR + initName + ".java");
                methodAroundComboBox.refresh();
                methodAroundComboBox.setSelected(name);
            }
        });
        super.doOKAction();
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }
}
