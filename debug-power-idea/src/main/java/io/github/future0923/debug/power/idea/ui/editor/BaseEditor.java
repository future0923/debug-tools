package io.github.future0923.debug.power.idea.ui.editor;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
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

    public BaseEditor(Project project, FileType fileType, String text) {
        super(text == null ? null : EditorFactory.getInstance().createDocument(StringUtil.convertLineSeparators(text)), project, fileType, false, false);
    }

    protected abstract String fileName();

    public static void setupTextFieldEditor(@NotNull EditorEx editor) {
        EditorSettings settings = editor.getSettings();
        settings.setFoldingOutlineShown(true);
        settings.setLineNumbersShown(true);
        settings.setIndentGuidesShown(true);
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
                    }
            );
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
        setupTextFieldEditor(editor);
        if (virtualFile != null) {
            editor.setFile(virtualFile);
        }
        onCreateEditor(editor);
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

    public Document createDocument(@Nullable final String text, @NotNull final FileType fileType) {
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

    protected void onCreateEditor(EditorEx editor) {

    }

    protected void onCreateDocument(PsiFile psiFile) {

    }
}
