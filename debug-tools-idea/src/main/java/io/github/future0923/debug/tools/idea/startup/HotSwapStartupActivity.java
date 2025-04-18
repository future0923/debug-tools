package io.github.future0923.debug.tools.idea.startup;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 项目打开时调用
 *
 * @author future0923
 */
public class HotSwapStartupActivity implements ProjectActivity {

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        StateUtils.setProjectOpenTime(project);
        return null;
    }
}
