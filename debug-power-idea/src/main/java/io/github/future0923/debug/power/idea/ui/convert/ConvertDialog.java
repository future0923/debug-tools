package io.github.future0923.debug.power.idea.ui.convert;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.ui.JsonEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * @author future0923
 */
public class ConvertDialog extends DialogWrapper {

    private final Project project;

    private final JsonEditor jsonEditor;

    private final ConvertType convertType;

    private ConvertPanel convertPanel;

    public ConvertDialog(@Nullable Project project, JsonEditor jsonEditor, ConvertType convertType) {
        super(project, true, IdeModalityType.IDE);
        this.project = project;
        this.jsonEditor = jsonEditor;
        this.convertType = convertType;
        setTitle(convertType.getDescription());
        setOKButtonText(convertType.getOkButtonText());
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        convertPanel = new ConvertPanel(project, jsonEditor, convertType);
        return convertPanel;
    }

    @Override
    protected void doOKAction() {
        if (ConvertType.IMPORT.equals(convertType)) {
            String text = convertPanel.getEditorTextField().getText();
            if (convertPanel.getJson().isSelected()) {
                jsonEditor.setText(DebugPowerJsonUtils.jsonConvertDebugPowerJson(text));
            } else if (convertPanel.getQuery().isSelected()){
                jsonEditor.setText(DebugPowerJsonUtils.queryConvertDebugPowerJson(text));
            }
        } else if (ConvertType.EXPORT.equals(convertType)) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(convertPanel.getEditorTextField().getText()), null);
        }
        super.doOKAction();
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }
}
