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
package io.github.future0923.debug.tools.idea.runner;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NlsActions;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.util.text.TextWithMnemonic;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class HotswapDebugExecutor extends Executor {

    public static final String EXECUTOR_ID = "DebugTools Debug Executor";

    @Override
    public @NotNull String getToolWindowId() {
        return getId();
    }

    @Override
    public @NotNull Icon getToolWindowIcon() {
        return getIcon();
    }

    @Override
    public @NotNull Icon getIcon() {
        return DebugToolsIcons.Hotswap.Off;
    }

    @Override
    public @NotNull Icon getRerunIcon() {
        return AllIcons.Actions.RestartDebugger;
    }

    @Override
    public Icon getDisabledIcon() {
        return IconLoader.getDisabledIcon(this.getIcon());
    }

    @Override
    public @NlsActions.ActionDescription String getDescription() {
        return "Hotswap debug with DebugTools";
    }

    @Override
    public @NotNull @NlsActions.ActionText String getActionName() {
        return EXECUTOR_ID;
    }

    @Override
    public @NotNull @NonNls String getId() {
        return EXECUTOR_ID;
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getStartActionText() {
        return "Hotswap with DebugTools";
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getStartActionText(@NotNull String configurationName) {
        String configName = StringUtil.isEmpty(configurationName) ? "" : " '" + shortenNameIfNeeded(configurationName) + "'";
        return TextWithMnemonic.parse("Hotswap").append(configName).append(" with DebugTools").toString();
    }

    @Override
    public @NonNls String getContextActionId() {
        return getId() + "-no-context-action-id";
    }

    @Override
    public @NonNls String getHelpId() {
        return null;
    }

    public static Executor getRunExecutorInstance() {
        return ExecutorRegistry.getInstance().getExecutorById(EXECUTOR_ID);
    }
}
