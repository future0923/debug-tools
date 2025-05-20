/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.idea.ui.convert;

import io.github.future0923.debug.tools.base.hutool.json.JSONObject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import io.github.future0923.debug.tools.common.enums.RunContentType;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.ui.main.MainJsonEditor;
import io.github.future0923.debug.tools.idea.utils.DebugToolsJsonElementUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;

/**
 * @author future0923
 */
public class ConvertDialog extends DialogWrapper {

    private final Project project;

    private final MainJsonEditor jsonEditor;

    private final ConvertType convertType;

    private ConvertPanel convertPanel;

    public ConvertDialog(@Nullable Project project, MainJsonEditor jsonEditor, ConvertType convertType) {
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
                        JSONObject jsonObject = DebugToolsJsonUtils.createJsonObject();
                        JSONObject argContent = DebugToolsJsonUtils.createJsonObject();
                        argContent.set("type", RunContentType.JSON_ENTITY.getType());
                        argContent.set("content", DebugToolsJsonUtils.parse(text));
                        jsonObject.set(parameter.getName(), argContent);
                        jsonEditor.setText(DebugToolsJsonUtils.toJsonPrettyStr(jsonObject));
                        super.doOKAction();
                        return;
                    }
                }
                jsonEditor.setText(DebugToolsJsonUtils.jsonConvertDebugToolsJson(text));
            } else if (convertPanel.getQuery().isSelected()) {
                jsonEditor.setText(DebugToolsJsonUtils.queryConvertDebugToolsJson(text));
            } else if (convertPanel.getPath().isSelected()) {
                List<String> args = Arrays.stream(jsonEditor.getPsiParameterList().getParameters())
                        .filter(psiParameter -> RunContentType.SIMPLE.getType().equals(DebugToolsJsonElementUtil.getContentType(psiParameter.getType())))
                        .map(PsiParameter::getName)
                        .toList();
                jsonEditor.setText(DebugToolsJsonUtils.pathConvertDebugToolsJson(text, args));
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
