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
package io.github.future0923.debug.tools.idea.startup;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.util.concurrency.AppExecutorUtil;
import io.github.future0923.debug.tools.idea.search.utils.HttpUrlUtils;
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
public class DebugToolsStartupActivity implements ProjectActivity {

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        StateUtils.setProjectOpenTime(project);
        ReadAction.nonBlocking(() -> {
                    HttpUrlUtils.getAllRequest(project);
                    return Unit.INSTANCE;
                })
                .inSmartMode(project)
                .coalesceBy(
                        DebugToolsStartupActivity.class, // 唯一来源
                        project,                         // 具体工程
                        "http-url-scan"                  // 任务标签
                )
                .submit(AppExecutorUtil.getAppExecutorService());
        return null;
    }
}
