/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
