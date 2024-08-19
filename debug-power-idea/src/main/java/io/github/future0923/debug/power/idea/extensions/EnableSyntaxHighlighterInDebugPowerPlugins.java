package io.github.future0923.debug.power.idea.extensions;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageUtil;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighterProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VirtualFile;
import io.github.future0923.debug.power.base.utils.DebugPowerStringUtils;
import io.github.future0923.debug.power.idea.constant.IdeaPluginProjectConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author future0923
 */
public class EnableSyntaxHighlighterInDebugPowerPlugins implements SyntaxHighlighterProvider {

    @Override
    public @Nullable SyntaxHighlighter create(@NotNull FileType fileType, @Nullable Project project, @Nullable VirtualFile file) {
        if (project == null || file == null) {
            return null;
        }
        String parent = FileUtilRt.toSystemIndependentName(file.getParent().getPath());
        if (DebugPowerStringUtils.isBlank(parent)) {
            return null;
        }
        if (!parent.equals(FileUtilRt.toSystemIndependentName(PathManager.getScratchPath()) + IdeaPluginProjectConstants.SCRATCH_PATH)) {
            return null;
        }
        Language language = LanguageUtil.getLanguageForPsi(project, file);
        if (language == null) {
            return null;
        }
        return SyntaxHighlighterFactory.getSyntaxHighlighter(language, project, file);
    }
}
