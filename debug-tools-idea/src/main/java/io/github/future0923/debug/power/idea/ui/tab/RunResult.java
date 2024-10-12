package io.github.future0923.debug.power.idea.ui.tab;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.power.common.enums.ResultClassType;

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
