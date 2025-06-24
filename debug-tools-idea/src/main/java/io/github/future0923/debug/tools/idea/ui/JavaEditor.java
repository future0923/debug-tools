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

import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiParameterList;
import com.intellij.ui.EditorTextField;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.GlobalContextKey;
/**
 * @author future0923
 */
@Getter
public class JavaEditor extends EditorTextField {

    public static final String FILE_NAME = "DebugToolsJavaFile.java";

    public static final GlobalContextKey<PsiParameterList> DEBUG_POWER_EDIT_CONTENT = GlobalContextKey.create("DebugToolsJavaFile");

    private static final FileType fileType = JavaFileType.INSTANCE;

    public JavaEditor(Project project) {
        super("", project, fileType);
        setDocument(createDocument(""));
        addSettingsProvider(editor -> {
            editor.setHorizontalScrollbarVisible(true);
            editor.setVerticalScrollbarVisible(true);
        });
    }

    @Override
    protected @NotNull EditorEx createEditor() {
        final EditorEx ex = super.createEditor();
        ex.setHighlighter(HighlighterFactory.createHighlighter(getProject(), JavaFileType.INSTANCE));
        ex.setEmbeddedIntoDialogWrapper(true);
        ex.setOneLineMode(false);
        return ex;
    }

    protected Document createDocument(String initText) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(getProject());
        final long stamp = System.currentTimeMillis();
        final PsiFile psiFile = factory.createFileFromText(FILE_NAME, fileType, initText, stamp, true, false);
        return PsiDocumentManager.getInstance(getProject()).getDocument(psiFile);
    }

}
