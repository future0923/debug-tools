package io.github.future0923.debug.power.idea.context;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.impl.java.stubs.impl.PsiParameterListStubImpl;
import com.intellij.psi.impl.source.PsiParameterListImpl;
import io.github.future0923.debug.power.idea.model.ParamCache;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.utils.DebugPowerActionUtil;
import lombok.Getter;

import java.util.Objects;

/**
 * @author future0923
 */
public class MethodDataContext {

    /**
     * 当前项目
     */
    private final Project project;

    /**
     * 类数据上下文
     */
    private final ClassDataContext classDataContext;

    @Getter
    private final PsiMethod psiMethod;

    @Getter
    private final String cacheKey;

    @Getter
    private final ParamCache cache;

    public MethodDataContext(ClassDataContext classDataContext, PsiMethod psiMethod, Project project) {
        this.project = project;
        this.cacheKey = DebugPowerActionUtil.genCacheKey(classDataContext.getPsiClass(), psiMethod);
        DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
        this.cache = settingState.getCache(cacheKey);
        this.psiMethod = psiMethod;
        this.classDataContext = classDataContext;
        //this.qualifiedMethodName = DebugPowerIdeaClassUtil.getMethodQualifiedName(psiMethod);
    }

    public PsiClass getPsiClass() {
        return classDataContext.getPsiClass();
    }

    public PsiParameterList getParamList() {
        if (Objects.isNull(psiMethod)) {
            return new PsiParameterListImpl(new PsiParameterListStubImpl(null));
        }
        return psiMethod.getParameterList();
    }
}
