package io.github.future0923.debug.tools.idea.ui.hotswap;

import io.github.future0923.debug.tools.idea.listener.data.BaseDataListener;
import io.github.future0923.debug.tools.idea.listener.data.event.DeployFileDataEvent;

/**
 * @author future0923
 */
public class ResetListener extends BaseDataListener<DeployFileDataEvent> {

    private final HotDeployDialog dialog;

    public ResetListener(HotDeployDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onEvent(DeployFileDataEvent event) {
        if (!DeployFileDataEvent.DeployFileType.Reset.equals(event.getFileType())) {
            return;
        }
        dialog.reset();
    }
}
