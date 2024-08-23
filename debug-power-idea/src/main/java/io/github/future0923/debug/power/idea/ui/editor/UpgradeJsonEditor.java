package io.github.future0923.debug.power.idea.ui.editor;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class UpgradeJsonEditor extends BaseEditor {

    public static final String FILE_NAME = "DebugPowerEditFile.json";

    /**
     * json格式
     */
    public static final FileType JSON_FILE_TYPE = JsonFileType.INSTANCE;

    public UpgradeJsonEditor(@NotNull Project project) {
        this(project, null);
    }

    public UpgradeJsonEditor(Project project, String text) {
        super(project, JSON_FILE_TYPE, text);
    }

    @Override
    protected String fileName() {
        return FILE_NAME;
    }
}
