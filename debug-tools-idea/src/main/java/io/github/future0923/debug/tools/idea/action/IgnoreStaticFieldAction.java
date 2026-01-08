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
package io.github.future0923.debug.tools.idea.action;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.concurrency.AppExecutorUtil;
import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.utils.HotswapIgnoreStaticFieldUtils;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 热重载忽略静态字段
 *
 * @author future0923
 */
public class IgnoreStaticFieldAction extends AnAction {

    private final static Key<PsiField> USER_DATE_ELEMENT_KEY = new Key<>("user.psi.Field");

    public IgnoreStaticFieldAction() {
        getTemplatePresentation().setText(DebugToolsBundle.message("action.hotswap.ignore.static.field"));
        getTemplatePresentation().setIcon(DebugToolsIcons.Hotswap.Ignore);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (null == project || editor == null) {
            throw new IllegalArgumentException("idea arg error (project or editor is null)");
        }
        ReadAction.nonBlocking(() -> {
                    PsiField psiField = null;
                    if (e.getDataContext() instanceof UserDataHolder) {
                        psiField = ((UserDataHolder) e.getDataContext()).getUserData(USER_DATE_ELEMENT_KEY);
                    }
                    if (psiField == null) {
                        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
                        psiField = PsiTreeUtil.getParentOfType(getElement(editor, file), PsiField.class);
                        if (psiField == null) {
                            throw new IllegalArgumentException("idea arg error (method is null)");
                        }
                    }
                    PsiClass psiClass = (PsiClass) psiField.getParent();
                    String className = psiClass.getQualifiedName();
                    String fieldName = psiField.getName();
                    return new DTO(className, fieldName, psiField.getContainingFile());
                })
                .withDocumentsCommitted(project)
                .submit(AppExecutorUtil.getAppExecutorService())
                .onSuccess(dto -> {
                    DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
                    String ignoreStaticFieldConfName = settingState.getIgnoreStaticFieldConfName();
                    String filePath;
                    // 当前没有选中
                    if (StrUtil.isBlank(ignoreStaticFieldConfName)) {
                        ignoreStaticFieldConfName = "default";
                    }
                    // 有没有default的配置
                    filePath = settingState.getIgnoreStaticFieldPathMap().get(ignoreStaticFieldConfName);
                    if (StrUtil.isBlank(filePath)) {
                        filePath = project.getBasePath() + IdeaPluginProjectConstants.IGNORE_STATIC_FIELD_DIR + "default.conf";
                    }
                    FileUtil.appendLines(List.of(dto.className() + "#" + dto.fieldName()), filePath, StandardCharsets.UTF_8);
                    settingState.setIgnoreStaticFieldConfName(ignoreStaticFieldConfName);
                    settingState.getIgnoreStaticFieldPathMap().put(ignoreStaticFieldConfName, filePath);
                    settingState.reloadIgnoreStaticFieldByPath(project);
                    ApplicationManager.getApplication().invokeLater(() -> DaemonCodeAnalyzer.getInstance(project).restart(dto.psiFile()));
                });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 当前项目
        Project project = e.getProject();
        if (project == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        // 当前编辑器
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        // 当前文件
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        // 获取光标所在属性
        PsiField field = PsiTreeUtil.getParentOfType(getElement(editor, file), PsiField.class);
        if (field == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        if (!field.hasModifierProperty(PsiModifier.STATIC)) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        if (field.getContainingClass() == null){
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        Map<String, Set<String>> ruleMap = settingState.getIgnoreStaticFieldRuleMap();
        boolean enabled;
        if (CollUtil.isEmpty(ruleMap)) {
            enabled = true;
        } else {
            String className = field.getContainingClass().getQualifiedName();
            String fieldName = field.getName();
            enabled = !HotswapIgnoreStaticFieldUtils.isIgnored(className, fieldName, ruleMap);
        }
        // 如果是启用状态，则将光标所在方法保存到数据上下文中
        if (enabled && e.getDataContext() instanceof UserDataHolder) {
            ((UserDataHolder) e.getDataContext()).putUserData(USER_DATE_ELEMENT_KEY, field);
        }
        // 启动禁用按钮
        e.getPresentation().setEnabledAndVisible(enabled);
    }

    @Nullable
    public static PsiElement getElement(Editor editor, PsiFile file) {
        if (editor == null || file == null) {
            return null;
        }
        // 获取光标模型 CaretModel 对象。
        CaretModel caretModel = editor.getCaretModel();
        // 获取光标当前的偏移量（即光标在文件中的位置）
        int position = caretModel.getOffset();
        // 根据光标的位置在文件中查找对应的 PsiElement 对象
        return file.findElementAt(position);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    private record DTO(String className, String fieldName, PsiFile psiFile) {
    }
}
