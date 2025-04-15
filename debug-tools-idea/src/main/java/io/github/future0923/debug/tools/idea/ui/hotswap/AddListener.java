package io.github.future0923.debug.tools.idea.ui.hotswap;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import io.github.future0923.debug.tools.idea.listener.data.BaseDataListener;
import io.github.future0923.debug.tools.idea.listener.data.event.DeployFileDataEvent;

import java.util.List;

/**
 * @author future0923
 */
public class AddListener extends BaseDataListener<DeployFileDataEvent> {

    private final Project project;

    private final HotDeployDialog dialog;

    public AddListener(Project project, HotDeployDialog dialog) {
        this.project = project;
        this.dialog = dialog;
    }

    @Override
    public void onEvent(DeployFileDataEvent event) {
        if (!DeployFileDataEvent.DeployFileType.Add.equals(event.getFileType())) {
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, true);
            String basePath = project.getBasePath();
            VirtualFile initialDirectory = io.github.future0923.debug.tools.idea.utils.VirtualFileUtil.getVirtualFileByPath(basePath);
            VirtualFile[] chosenFiles = FileChooser.chooseFiles(descriptor, project, initialDirectory);
            if (null != basePath && !basePath.endsWith("/")) {
                basePath = basePath + "/";
            }
            List<String> hotUndoList = dialog.getHotUndoList();
            for (VirtualFile file : chosenFiles) {
                if (!hotUndoList.contains(file.getPath())) {
                    if (!"java".equals(file.getExtension())) {
                        continue;
                    }
                    String path = file.getPath();
                    if (null != basePath) {
                        path = path.replaceAll(basePath, "");
                    }
                    dialog.getHotUndoShowList().addElement(path);
                    hotUndoList.add(file.getPath());
                }
            }
        });
    }
}
