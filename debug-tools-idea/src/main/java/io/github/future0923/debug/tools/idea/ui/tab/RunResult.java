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
package io.github.future0923.debug.tools.idea.ui.tab;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.common.enums.ResultClassType;

/**
 * @author future0923
 */
public class RunResult extends ResultTabbedPane{

    public RunResult(Project project, String printResult, String offsetPath, ResultClassType resultClassType) {
        super(project, printResult, offsetPath, resultClassType);
    }

    @Override
    protected boolean jsonTab() {
        return true;
    }

    @Override
    protected boolean debugTab() {
        return true;
    }
}
