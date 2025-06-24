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
