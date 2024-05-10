package io.github.future0923.debug.power.idea.ui;

import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiParameterList;
import com.intellij.ui.EditorTextField;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.utils.DebugPowerJsonElementUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.incremental.GlobalContextKey;

/**
 * @author future0923
 */
public class JsonEditor extends EditorTextField {

    public static String FILE_NAME = "DebugPowerEditFile.json";

    public static GlobalContextKey<PsiParameterList> DEBUG_POWER_EDIT_CONTENT = GlobalContextKey.create("DebugPowerEditContent");

    private final FileType fileType = JsonFileType.INSTANCE;

    @Nullable
    @Getter
    private final PsiParameterList psiParameterList;

    public JsonEditor(String cacheText, @Nullable PsiParameterList psiParameterList, Project project) {
        super("", project, JsonFileType.INSTANCE);
        this.psiParameterList = psiParameterList;

        if (StringUtils.isBlank(cacheText)) {
            setDocument(createDocument(getJsonText(psiParameterList)));
        } else {
            setDocument(createDocument(cacheText));
        }

        addSettingsProvider(editor -> {
            editor.setHorizontalScrollbarVisible(true);
            editor.setVerticalScrollbarVisible(true);
        });
    }

    public String getJsonText(@Nullable PsiParameterList psiParameterList) {
        return DebugPowerJsonElementUtil.getJsonText(psiParameterList);
    }

    public void regenerateJsonText() {
        setText(getJsonText(psiParameterList));
    }

    public void prettyJsonText() {
        setText(DebugPowerJsonUtils.pretty(getText()));
    }

    @Override
    protected @NotNull EditorEx createEditor() {
        final EditorEx ex = super.createEditor();

        ex.setHighlighter(HighlighterFactory.createHighlighter(getProject(), JsonFileType.INSTANCE));
        ex.setEmbeddedIntoDialogWrapper(true);
        ex.setOneLineMode(false);

        ex.putUserData(DEBUG_POWER_EDIT_CONTENT, psiParameterList);

        return ex;
    }

    protected Document createDocument(String initText) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(getProject());
        final long stamp = System.currentTimeMillis();
        final PsiFile psiFile = factory.createFileFromText(FILE_NAME, fileType, initText, stamp, true, false);

        psiFile.putUserData(DEBUG_POWER_EDIT_CONTENT, psiParameterList);

        return PsiDocumentManager.getInstance(getProject()).getDocument(psiFile);
    }
}
