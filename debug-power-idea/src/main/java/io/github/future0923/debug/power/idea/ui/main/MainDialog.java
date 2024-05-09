package io.github.future0923.debug.power.idea.ui.main;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.context.MethodDataContext;
import io.github.future0923.debug.power.idea.ui.JsonEditor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author future0923
 */
public class MainDialog extends DialogWrapper {

    private final Project project;

    private final MethodDataContext methodDataContext;

    private MainPanel mainPanel;

    @Setter
    private BiConsumer<String, String> okAction;

    public MainDialog(MethodDataContext methodDataContext, @Nullable Project project) {
        super(project, true, IdeModalityType.MODELESS);
        this.project = project;
        this.methodDataContext = methodDataContext;
        setTitle("Quick Debug");
        setOKButtonText("Run");
        setOKButtonIcon(AllIcons.Actions.RunAll);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        mainPanel = new MainPanel(project, methodDataContext);
        return mainPanel;
    }

    @Override
    protected void doOKAction() {
        if (Objects.nonNull(okAction)) {
            JsonEditor editor = mainPanel.getEditor();
            String auth = mainPanel.getAuthField().getText();
            okAction.accept(auth, DebugPowerJsonUtils.compress(editor.getText()));
        }
        super.doOKAction();
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }
}
