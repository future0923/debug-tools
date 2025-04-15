package io.github.future0923.debug.tools.idea.ui.hotswap;

import com.intellij.ui.components.JBList;
import io.github.future0923.debug.tools.idea.listener.data.BaseDataListener;
import io.github.future0923.debug.tools.idea.listener.data.event.DeployFileDataEvent;

/**
 * @author future0923
 */
public class DeleteListener extends BaseDataListener<DeployFileDataEvent> {

    private final HotDeployDialog dialog;

    private final JBList<String> jbList;

    public DeleteListener(HotDeployDialog dialog, JBList<String> jbList) {
        this.dialog = dialog;
        this.jbList = jbList;
    }

    @Override
    public void onEvent(DeployFileDataEvent event) {
        if (!DeployFileDataEvent.DeployFileType.Delete.equals(event.getFileType())) {
            return;
        }
        int selectedIndex = jbList.getSelectedIndex();
        if (selectedIndex != -1) {
            dialog.getHotUndoList().remove(selectedIndex);
            dialog.getHotUndoShowList().remove(selectedIndex);
            jbList.setSelectedIndex(selectedIndex - 1);
        }
    }
}
