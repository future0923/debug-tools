package io.github.future0923.debug.power.idea.ui.editor;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;

/**
 * @author future0923
 */
public class TextEditor extends BaseEditor {

    public static final String FILE_NAME = "DebugPowerEditFile.text";

    public static final FileType TEXT_FILE_TYPE = FileTypes.PLAIN_TEXT;

    public TextEditor(Project project) {
        this(project, null);
    }

    public TextEditor(Project project, String text) {
        super(project, TEXT_FILE_TYPE, text);
    }

    @Override
    protected String fileName() {
        return FILE_NAME;
    }
}
