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

import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiParameterList;
import com.intellij.util.concurrency.AppExecutorUtil;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.setting.GenParamType;
import io.github.future0923.debug.tools.idea.ui.editor.JsonEditor;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import io.github.future0923.debug.tools.idea.utils.DebugToolsJsonElementUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.incremental.GlobalContextKey;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author future0923
 */
@Getter
public class MainJsonEditor extends JsonEditor {

    private static final String STATUS_FIELD_NAME = "_status";

    private final PsiParameterList psiParameterList;

    public static final String FILE_NAME = "DebugToolsContentEditFile.json";

    public static final GlobalContextKey<PsiParameterList> DEBUG_POWER_EDIT_CONTENT = GlobalContextKey.create("DebugToolsEditContent");

    private final AtomicInteger generationVersion = new AtomicInteger();

    @Getter
    private volatile boolean generating;

    public MainJsonEditor(String cacheText, PsiParameterList psiParameterList, Project project) {
        super(project, "");
        this.psiParameterList = psiParameterList;
        if (StringUtils.isBlank(cacheText)) {
            DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
            regenerateJsonText(settingState.getDefaultGenParamType(), true);
        } else {
            setText(cacheText);
        }
    }

    public String getJsonText(@Nullable PsiParameterList psiParameterList, GenParamType genParamType) {
        return DebugToolsJsonElementUtil.getJsonText(psiParameterList, genParamType);
    }

    public void regenerateJsonText(GenParamType type) {
        regenerateJsonText(type, false);
    }

    public void prettyJsonText() {
        setText(DebugToolsJsonUtils.pretty(getText()));
    }

    private void regenerateJsonText(GenParamType type, boolean preserveManualText) {
        int currentGenerationVersion = generationVersion.incrementAndGet();
        String previousText = getText();
        String loadingText = statusText("Generating parameters...");
        generating = true;
        setText(loadingText);
        ReadAction.nonBlocking(() -> buildJsonResult(type))
                .withDocumentsCommitted(getProject())
                .finishOnUiThread(ModalityState.any(), result -> {
                    if (generationVersion.get() != currentGenerationVersion) {
                        return;
                    }
                    generating = false;
                    if (preserveManualText && !StringUtils.equals(getText(), loadingText)) {
                        return;
                    }
                    if (result.success()) {
                        setText(result.text());
                    } else {
                        if (preserveManualText) {
                            setText("{}");
                        } else {
                            setText(previousText);
                        }
                        DebugToolsNotifierUtil.notifyError(getProject(), result.errorMessage());
                    }
                })
                .submit(AppExecutorUtil.getAppExecutorService());
    }

    private GenerateResult buildJsonResult(GenParamType type) {
        if (psiParameterList == null) {
            return new GenerateResult(null, "当前记录缺少方法参数信息，无法重新生成参数", false);
        }
        try {
            String text;
            if (GenParamType.SIMPLE.equals(type)) {
                text = DebugToolsJsonElementUtil.getSimpleText(psiParameterList);
            } else {
                text = getJsonText(psiParameterList, type);
            }
            return new GenerateResult(text, null, true);
        } catch (Exception ex) {
            return new GenerateResult(null, "参数生成失败: " + ex.getMessage(), false);
        }
    }

    private String statusText(String status) {
        return DebugToolsJsonUtils.createJsonObject().set(STATUS_FIELD_NAME, status).toJSONString(4);
    }

    private record GenerateResult(String text, String errorMessage, boolean success) {
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
