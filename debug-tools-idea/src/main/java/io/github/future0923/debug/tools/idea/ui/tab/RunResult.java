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
