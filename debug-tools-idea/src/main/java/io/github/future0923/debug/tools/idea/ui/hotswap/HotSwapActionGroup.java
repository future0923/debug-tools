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
