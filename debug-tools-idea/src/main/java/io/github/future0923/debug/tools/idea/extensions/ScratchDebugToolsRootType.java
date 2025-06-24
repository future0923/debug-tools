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

import com.intellij.ide.scratch.RootType;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.io.FileUtilRt;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;

/**
 * @author future0923
 */
public class ScratchDebugToolsRootType extends RootType {

    protected ScratchDebugToolsRootType() {
        super(IdeaPluginProjectConstants.ROOT_TYPE_ID, IdeaPluginProjectConstants.ROOT_TYPE_DISPLAY_NAME);
        System.setProperty(
                PathManager.PROPERTY_SCRATCH_PATH + "/" + IdeaPluginProjectConstants.ROOT_TYPE_ID,
                FileUtilRt.toSystemIndependentName(PathManager.getScratchPath()) + IdeaPluginProjectConstants.SCRATCH_PATH
        );
    }
}
