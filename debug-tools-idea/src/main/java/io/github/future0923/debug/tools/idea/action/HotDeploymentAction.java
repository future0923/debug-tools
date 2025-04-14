package io.github.future0923.debug.tools.idea.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.DumbAwareAction;
import io.github.future0923.debug.tools.idea.ui.hotswap.HotSwapDialog;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class HotDeploymentAction extends DumbAwareAction {

    public HotDeploymentAction() {
        getTemplatePresentation().setIcon(DebugToolsIcons.Hotswap.Deploy);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        FileDocumentManager.getInstance().saveAllDocuments();
        HotSwapDialog hotSwapDialog = new HotSwapDialog(e.getProject());
        hotSwapDialog.show();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
