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
