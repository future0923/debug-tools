package io.github.future0923.debug.power.idea.extensions;

import com.intellij.ide.scratch.RootType;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.io.FileUtilRt;
import io.github.future0923.debug.power.idea.constant.IdeaPluginProjectConstants;

/**
 * @author future0923
 */
public class ScratchDebugPowerRootType extends RootType {

    protected ScratchDebugPowerRootType() {
        super(IdeaPluginProjectConstants.ROOT_TYPE_ID, IdeaPluginProjectConstants.ROOT_TYPE_DISPLAY_NAME);
        System.setProperty(
                PathManager.PROPERTY_SCRATCH_PATH + "/" + IdeaPluginProjectConstants.ROOT_TYPE_ID,
                FileUtilRt.toSystemIndependentName(PathManager.getScratchPath()) + IdeaPluginProjectConstants.SCRATCH_PATH
        );
    }
}
