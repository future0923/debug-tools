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
package io.github.future0923.debug.tools.idea.ui.editor;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.ui.EditorTextField;
import com.intellij.util.LocalTimeCounter;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.border.Border;

/**
 * @author future0923
 */
public abstract class BaseEditor extends EditorTextField {

    private VirtualFile virtualFile;

    private EditorEx editorEx;

    public BaseEditor(Project project, FileType fileType, String text) {
        super(text == null ? null : EditorFactory.getInstance().createDocument(StringUtil.convertLineSeparators(text)), project, fileType, false, false);
    }

    protected abstract String fileName();

    public void setupEditor(@NotNull EditorEx editor) {
        setting(editor.getSettings());
        editor.setHorizontalScrollbarVisible(true);
        editor.setVerticalScrollbarVisible(true);
    }

    public void setText(@Nullable final String text, @NotNull final FileType fileType) {
        super.setFileType(fileType);
        Document document = createDocument(text, fileType);
        setDocument(document);
        PsiFile psiFile = PsiDocumentManager.getInstance(getProject()).getPsiFile(document);
        if (psiFile != null) {
            WriteCommandAction.runWriteCommandAction(
                    getProject(),
                    () -> {
                        CodeStyleManager.getInstance(getProject()).reformat(psiFile);
                        moveToTop();
                    }
            );
        }
    }

    private void moveToTop() {
        if (editorEx != null) {
            editorEx.getCaretModel().moveToLogicalPosition(new LogicalPosition(0, 0));
            // 获取 ScrollingModel 并滚动到顶部
            ScrollingModel scrollingModel = editorEx.getScrollingModel();
            scrollingModel.scrollTo(new LogicalPosition(0, 0), ScrollType.CENTER);
        }
    }

    @Override
    public void setFileType(@NotNull FileType fileType) {
        setNewDocumentAndFileType(fileType, createDocument(getText(), fileType));
    }

    @Override
    protected Document createDocument() {
        return createDocument("", getFileType());
    }

    private void initOneLineMode(@NotNull final EditorEx editor) {
        editor.setOneLineMode(false);
        editor.setColorsScheme(editor.createBoundColorSchemeDelegate(null));
        editor.getSettings().setCaretRowShown(false);
    }

    @Override
    protected @NotNull EditorEx createEditor() {
        EditorEx editor = super.createEditor();
        initOneLineMode(editor);
        setupEditor(editor);
        if (virtualFile != null) {
            editor.setFile(virtualFile);
        }
        onCreateEditor(editor);
        this.editorEx = editor;
        moveToTop();
        return editor;
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);
        if (getEditor() instanceof EditorEx) {
            initOneLineMode(((EditorEx) getEditor()));
        }
    }

    @Override
    public void setBorder(Border border) {
        super.setBorder(JBUI.Borders.empty());
    }

    private Document createDocument(@Nullable final String text, @NotNull final FileType fileType) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(getProject());
        final long stamp = LocalTimeCounter.currentTime();
        final PsiFile psiFile = factory.createFileFromText(
                fileName(),
                fileType,
                text == null ? "" : text,
                stamp,
                true,
                false
        );
        onCreateDocument(psiFile);
        virtualFile = psiFile.getVirtualFile();
        return PsiDocumentManager.getInstance(getProject()).getDocument(psiFile);
    }

    protected void setting(EditorSettings settings) {
        settings.setFoldingOutlineShown(true);
        settings.setLineNumbersShown(true);
        settings.setIndentGuidesShown(true);
    }

    protected void onCreateEditor(EditorEx editor) {

    }

    protected void onCreateDocument(PsiFile psiFile) {

    }
}
