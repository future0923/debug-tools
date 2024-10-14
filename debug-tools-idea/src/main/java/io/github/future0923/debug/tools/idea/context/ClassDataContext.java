package io.github.future0923.debug.tools.idea.context;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import lombok.Getter;

public class ClassDataContext {

    private final Project project;

    @Getter
    private final PsiClass psiClass;


    public ClassDataContext(PsiClass psiClass, Project project) {
        this.project = project;
        this.psiClass = psiClass;
    }
}
