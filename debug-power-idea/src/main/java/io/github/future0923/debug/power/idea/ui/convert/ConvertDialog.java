package io.github.future0923.debug.power.idea.ui.convert;

import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiTypes;
import io.github.future0923.debug.power.common.enums.RunContentType;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.ui.JsonEditor;
import io.github.future0923.debug.power.idea.utils.DebugPowerJsonElementUtil;
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
                PsiParameterList psiParameterList = jsonEditor.getPsiParameterList();
                if (psiParameterList != null && psiParameterList.getParametersCount() == 1) {
                    PsiParameter parameter = psiParameterList.getParameter(0);
                    if (parameter != null && parameter.getType() instanceof PsiClassType) {
                        JsonObject jsonObject = new JsonObject();
                        JsonObject argContent = new JsonObject();
                        argContent.addProperty("type", RunContentType.JSON_ENTITY.getType());
                        argContent.add("content", DebugPowerJsonUtils.getInstance().fromJson(text, JsonObject.class));
                        jsonObject.add(parameter.getName(), argContent);
                        jsonEditor.setText(DebugPowerJsonUtils.getInstancePretty().toJson(jsonObject));
                        super.doOKAction();
                        return;
                    }
                }
                jsonEditor.setText(DebugPowerJsonUtils.jsonConvertDebugPowerJson(text));
            } else if (convertPanel.getQuery().isSelected()) {
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
