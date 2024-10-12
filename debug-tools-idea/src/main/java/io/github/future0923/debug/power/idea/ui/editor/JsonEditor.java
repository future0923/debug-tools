package io.github.future0923.debug.power.idea.ui.editor;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author future0923
 */
public class JsonEditor extends BaseEditor {

    public static final String FILE_NAME = "DebugPowerEditFile.json";

    /**
     * json格式
     */
    public static final FileType JSON_FILE_TYPE = JsonFileType.INSTANCE;

    public JsonEditor(@NotNull Project project) {
        this(project, null);
    }

    public JsonEditor(Project project, String text) {
        super(project, JSON_FILE_TYPE, text);
    }

    @Override
    protected String fileName() {
        return FILE_NAME;
    }

    @Override
    public void setText(@Nullable String text) {
        setText(text, JSON_FILE_TYPE);
    }
}
