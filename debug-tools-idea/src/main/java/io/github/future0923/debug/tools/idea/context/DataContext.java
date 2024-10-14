package io.github.future0923.debug.tools.idea.context;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

/**
 * @author future0923
 */
@Service(value = Service.Level.PROJECT)
public final class DataContext {

    private final Project project;

    public DataContext(Project project) {
        this.project = project;
    }

    public static DataContext instance(Project project) {
        return project.getService(DataContext.class);
    }

    public ClassDataContext getClassDataContext(PsiClass psiClass) {
        return new ClassDataContext(psiClass, project);
    }
}
