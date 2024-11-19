package io.github.future0923.debug.tools.idea.startup;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Anchor;
import com.intellij.openapi.actionSystem.Constraints;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import io.github.future0923.debug.tools.idea.ui.hotswap.HotSwapActionGroup;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author future0923
 */
public class HotSwapStartupActivity implements ProjectActivity {

    private static volatile boolean alreadyCreated = false;

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        //createToolBar();
        return null;
    }

    public static void createToolBar() {
        // Check if the toolbar has already been created.
        if (!alreadyCreated) {
            // Get an instance of ActionManager to manage toolbar actions.
            ActionManager actionManager = ActionManager.getInstance();
            try {
                DefaultActionGroup runToolbarMainActionGroup = (DefaultActionGroup)actionManager.getAction("RunToolbarMainActionGroup");
                if (null == runToolbarMainActionGroup) {
                    return;
                }
                HotSwapActionGroup toolBarGroup = new HotSwapActionGroup();
                AnAction switchStateBar = actionManager.getAction("switchStateBar");
                if (null != switchStateBar) {
                    toolBarGroup.add(switchStateBar);
                }
                runToolbarMainActionGroup.addAction(toolBarGroup, new Constraints(Anchor.AFTER, "MoreRunToolbarActions"), actionManager);
            } catch (Throwable var7) {
                Throwable e = var7;
                e.printStackTrace();
            }
        }
    }
}
