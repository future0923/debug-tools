package io.github.future0923.debug.tools.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import io.github.future0923.debug.tools.common.exception.SocketCloseException;
import io.github.future0923.debug.tools.common.protocal.packet.request.DynamicCompilerRequestPacket;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class HotSwapDynamicCompilerAction extends AnAction {

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
        DynamicCompilerRequestPacket packet = new DynamicCompilerRequestPacket();
        packet.add(editor.getVirtualFile().getName().replace(".java", ""), content);
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
}
