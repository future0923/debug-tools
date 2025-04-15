package io.github.future0923.debug.tools.idea.ui.hotswap;

import io.github.future0923.debug.tools.idea.listener.data.BaseDataListener;
import io.github.future0923.debug.tools.idea.listener.data.event.DeployFileDataEvent;

/**
 * @author future0923
 */
public class ClearListener extends BaseDataListener<DeployFileDataEvent> {

    private final HotDeployDialog dialog;

    public ClearListener(HotDeployDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onEvent(DeployFileDataEvent event) {
        if (!DeployFileDataEvent.DeployFileType.Clear.equals(event.getFileType())) {
            return;
        }
        dialog.getHotUndoList().clear();
        dialog.getHotUndoShowList().clear();
    }
}
