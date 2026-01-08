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
package io.github.future0923.debug.tools.idea.line;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiModifier;
import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.utils.HotswapIgnoreStaticFieldUtils;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * 热重载忽略字段LineMarker
 *
 * @author future0923
 */
public class IgnoreStaticFieldLineMarkerProvider implements LineMarkerProvider {

    @Override
    public @Nullable LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement psiElement) {
        if (psiElement instanceof PsiIdentifier
                && psiElement.getParent() instanceof PsiField field) {
            if (!field.hasModifierProperty(PsiModifier.STATIC)) {
                return null;
            }
            if (field.getContainingClass() == null) {
                return null;
            }
            DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(psiElement.getProject());
            if (StrUtil.isBlank(settingState.getIgnoreStaticFieldConfName())) {
                return null;
            }
            Map<String, Set<String>> ruleMap = settingState.getIgnoreStaticFieldRuleMap();
            if (CollUtil.isEmpty(ruleMap)) {
                return null;
            }
            String className = field.getContainingClass().getQualifiedName();
            String fieldName = field.getName();
            if (HotswapIgnoreStaticFieldUtils.isIgnored(className, fieldName, ruleMap)) {
                return new LineMarkerInfo<>(
                        psiElement,
                        psiElement.getTextRange(),
                        DebugToolsIcons.Hotswap.Ignore,
                        e -> DebugToolsBundle.message("action.hotswap.ignore.static.field.gutter"),
                        (mouseEvent, elt) -> {
                            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                                String pathName = settingState.getIgnoreStaticFieldPathMap().get(settingState.getIgnoreStaticFieldConfName());
                                if (StrUtil.isNotBlank(pathName)) {
                                    HotswapIgnoreStaticFieldUtils.remove(new File(pathName), className, fieldName);
                                    settingState.reloadIgnoreStaticFieldByPath(psiElement.getProject());
                                    ApplicationManager.getApplication().invokeLater(() -> {
                                        PsiFile file = psiElement.getContainingFile();
                                        if (file == null || !file.isValid()) {
                                            return;
                                        }
                                        DaemonCodeAnalyzer.getInstance(psiElement.getProject()).restart(file);
                                    });
                                }
                            });
                        },
                        GutterIconRenderer.Alignment.RIGHT,
                        () -> DebugToolsBundle.message("action.hotswap.ignore.static.field.gutter")
                );
            }
        }
        return null;
    }
}
