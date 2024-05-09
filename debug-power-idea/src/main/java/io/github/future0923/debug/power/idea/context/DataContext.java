package io.github.future0923.debug.power.idea.context;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import io.github.future0923.debug.power.idea.utils.DebugPowerIdeaClassUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
@Service(value = Service.Level.PROJECT)
public final class DataContext {

    private final Project project;
    private final Map<String, ClassDataContext> contextMap;

    public DataContext(Project project) {
        this.project = project;
        this.contextMap = new ConcurrentHashMap<>();
    }

    public static DataContext instance(Project project) {
        return project.getService(DataContext.class);
    }

    public ClassDataContext getClassDataContext(String qualifiedClassName) {
        Objects.requireNonNull(qualifiedClassName);

        ClassDataContext classDataContext = contextMap.get(qualifiedClassName);
        if (classDataContext == null || classDataContext.getPsiClass() == null || !classDataContext.getPsiClass().isValid()) {
            PsiClass psiClass = DebugPowerIdeaClassUtil.findClass(project, qualifiedClassName);
            return new ClassDataContext(psiClass, project);
        }
        return classDataContext;
    }
}
