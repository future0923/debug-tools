package io.github.future0923.debug.power.idea.ui;

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

    public static final String FILE_NAME = "DebugPowerJavaFile.java";

    public static final GlobalContextKey<PsiParameterList> DEBUG_POWER_EDIT_CONTENT = GlobalContextKey.create("DebugPowerJavaFile");

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
