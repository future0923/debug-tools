/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.idea.ui.main;

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiParameterList;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.setting.GenParamType;
import io.github.future0923.debug.tools.idea.ui.editor.JsonEditor;
import io.github.future0923.debug.tools.idea.utils.DebugToolsJsonElementUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.incremental.GlobalContextKey;

/**
 * @author future0923
 */
@Getter
public class MainJsonEditor extends JsonEditor {

    private final PsiParameterList psiParameterList;

    public static final String FILE_NAME = "DebugToolsContentEditFile.json";

    public static final GlobalContextKey<PsiParameterList> DEBUG_POWER_EDIT_CONTENT = GlobalContextKey.create("DebugToolsEditContent");

    public MainJsonEditor(String cacheText, PsiParameterList psiParameterList, Project project) {
        super(project, "");
        this.psiParameterList = psiParameterList;
        if (StringUtils.isBlank(cacheText)) {
            DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
            setText(getJsonText(psiParameterList, settingState.getDefaultGenParamType()));
        } else {
            setText(cacheText);
        }
    }

    public String getJsonText(@Nullable PsiParameterList psiParameterList, GenParamType genParamType) {
        return DebugToolsJsonElementUtil.getJsonText(psiParameterList, genParamType);
    }

    public void regenerateJsonText(GenParamType type) {
        if (GenParamType.SIMPLE.equals(type)) {
            setText(DebugToolsJsonElementUtil.getSimpleText(psiParameterList));
        } else {
            setText(getJsonText(psiParameterList, type));
        }
    }

    public void prettyJsonText() {
        setText(DebugToolsJsonUtils.pretty(getText()));
    }

    @Override
    protected String fileName() {
        return FILE_NAME;
    }

    @Override
    protected void onCreateEditor(EditorEx editor) {
        editor.putUserData(DEBUG_POWER_EDIT_CONTENT, psiParameterList);
    }

    @Override
    protected void onCreateDocument(PsiFile psiFile) {
        psiFile.putUserData(DEBUG_POWER_EDIT_CONTENT, psiParameterList);
    }
}
