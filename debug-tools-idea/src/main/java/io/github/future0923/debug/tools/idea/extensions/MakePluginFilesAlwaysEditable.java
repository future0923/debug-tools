package io.github.future0923.debug.tools.idea.extensions;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.fileEditor.impl.NonProjectFileWritingAccessExtension;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VirtualFile;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class MakePluginFilesAlwaysEditable implements NonProjectFileWritingAccessExtension {

    @Override
    public boolean isWritable(@NotNull VirtualFile file) {
        String parent = FileUtilRt.toSystemIndependentName(file.getParent().getPath());
        if (DebugToolsStringUtils.isBlank(parent)) {
            return false;
        }
        return parent.equals(FileUtilRt.toSystemIndependentName(PathManager.getScratchPath()) + IdeaPluginProjectConstants.SCRATCH_PATH);
    }
}
