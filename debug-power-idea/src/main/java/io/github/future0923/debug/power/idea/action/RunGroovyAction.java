package io.github.future0923.debug.power.idea.action;

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
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class RunGroovyAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        // 获取当前编辑的文件
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        // 保存当前文件
        FileDocumentManager.getInstance().saveDocument(editor.getDocument());
        //VirtualFile currentFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
        //if (currentFile == null) return;
        String content = editor.getDocument().getText();
        Messages.showErrorDialog(content, "文件内容");
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
            presentation.setText("Run '" + file.getName() + "' With Debug Power");
            presentation.setEnabledAndVisible(true);
        } else {
            presentation.setEnabledAndVisible(false);
        }
    }
}
