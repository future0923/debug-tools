package io.github.future0923.debug.power.idea.ui.groovy;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.LanguageTextField;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.GroovyLanguage;

import javax.swing.*;

/**
 * @author future0923
 */
public class GroovyDiaLog extends DialogWrapper {

    private final Project project;

    public GroovyDiaLog(@Nullable Project project) {
        super(project, true, IdeModalityType.MODELESS);
        this.project = project;
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        GroovyLanguage instance = GroovyLanguage.INSTANCE;
        return new LanguageTextField(instance, project, "def a = 1");
    }
}
