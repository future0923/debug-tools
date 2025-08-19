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

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class HotSwapSwitchAction extends DumbAwareAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        settingState.setHotswap(!settingState.getHotswap());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        if (settingState.getHotswap()) {
            presentation.setText(DebugToolsBundle.message("action.hotswap.enable.text"));
            presentation.setDescription(DebugToolsBundle.message("action.hotswap.enable.description"));
            presentation.setIcon(DebugToolsIcons.Hotswap.On);
        } else {
            presentation.setText(DebugToolsBundle.message("action.hotswap.disable.text"));
            presentation.setDescription(DebugToolsBundle.message("action.hotswap.disable.description"));
            presentation.setIcon(DebugToolsIcons.Hotswap.Off);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
