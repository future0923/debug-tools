package io.github.future0923.debug.tools.idea.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import io.github.future0923.debug.tools.common.exception.SocketCloseException;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunGroovyScriptRequestPacket;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class RunGroovyAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        // 获取当前编辑的文件
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        // 保存当前文件
        FileDocumentManager.getInstance().saveDocument(editor.getDocument());
        String content = editor.getDocument().getText();
        RunGroovyScriptRequestPacket packet = new RunGroovyScriptRequestPacket();
        packet.setScript(content);
        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(project);
        if (info == null) {
            Messages.showErrorDialog("Run attach first", "执行失败");
            DebugToolsToolWindowFactory.showWindow(project, null);
            return;
        }
        try {
            info.getClient().getHolder().send(packet);
        } catch (SocketCloseException e) {
            Messages.showErrorDialog("Socket close", "执行失败");
            DebugToolsToolWindowFactory.showWindow(project, null);
        } catch (Exception e) {
            Messages.showErrorDialog("Socket send error " + e.getMessage(), "执行失败");
            DebugToolsToolWindowFactory.showWindow(project, null);
        }
    }


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Presentation presentation = e.getPresentation();
        if (project == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        // 当前文件
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (file == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        if ("Groovy".equalsIgnoreCase(file.getFileType().getName())) {
            presentation.setText("Run '" + file.getName() + "' With Debug Tools");
            presentation.setEnabledAndVisible(true);
        } else {
            presentation.setEnabledAndVisible(false);
        }
    }
}
