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
package io.github.future0923.debug.tools.idea.ui.hotswap;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author future0923
 */
public class HotSwapActionGroup extends DefaultActionGroup {

    public HotSwapActionGroup() {
    }

    public HotSwapActionGroup(AnAction @NotNull ... actions) {
        super(actions);
    }

    public HotSwapActionGroup(@NotNull List<? extends AnAction> actions) {
        super(actions);
    }

    public HotSwapActionGroup(@NotNull Supplier<@NlsActions.ActionText String> name, @NotNull List<? extends AnAction> actions) {
        super(name, actions);
    }

    public HotSwapActionGroup(@Nullable @NlsActions.ActionText String name, @NotNull List<? extends AnAction> actions) {
        super(name, actions);
    }

    public HotSwapActionGroup(@Nullable @NlsActions.ActionText String shortName, boolean popup) {
        super(shortName, popup);
    }

    public HotSwapActionGroup(@NotNull Supplier<@NlsActions.ActionText String> shortName, boolean popup) {
        super(shortName, popup);
    }

    public HotSwapActionGroup(@Nullable @NlsActions.ActionText String text, @Nullable @NlsActions.ActionDescription String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    public HotSwapActionGroup(@NotNull Supplier<@NlsActions.ActionText String> dynamicText, @NotNull Supplier<@NlsActions.ActionDescription String> dynamicDescription, @Nullable Icon icon) {
        super(dynamicText, dynamicDescription, icon);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText("123");
    }
}
