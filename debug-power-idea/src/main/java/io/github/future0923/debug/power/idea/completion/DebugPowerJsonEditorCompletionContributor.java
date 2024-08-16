package io.github.future0923.debug.power.idea.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.codeinsight.JsonCompletionContributor;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import io.github.future0923.debug.power.common.enums.RunContentType;
import io.github.future0923.debug.power.idea.ui.JsonEditor;
import io.github.future0923.debug.power.idea.utils.DebugPowerJsonElementUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author future0923
 */
public class DebugPowerJsonEditorCompletionContributor extends JsonCompletionContributor {

    private final List<LookupElement> KEY = Arrays.asList(LookupElementBuilder.create("type"), LookupElementBuilder.create("content"));
    private final List<LookupElement> VALUE = Arrays.asList(
            LookupElementBuilder.create(RunContentType.SIMPLE.getType()),
            LookupElementBuilder.create(RunContentType.ENUM.getType()),
            LookupElementBuilder.create(RunContentType.JSON_ENTITY.getType()),
            LookupElementBuilder.create(RunContentType.LAMBDA.getType()),
            LookupElementBuilder.create(RunContentType.BEAN.getType()),
            LookupElementBuilder.create(RunContentType.RESPONSE.getType()),
            LookupElementBuilder.create(RunContentType.RESPONSE.getType()),
            LookupElementBuilder.create(RunContentType.FILE.getType()),
            LookupElementBuilder.create(RunContentType.CLASS.getType())
    );

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
        if (!parameters.getOriginalFile().getName().equals(JsonEditor.FILE_NAME)) {
            return;
        }
        // 非key不做处理
        if (!DebugPowerJsonElementUtil.isJsonKey(parameters.getPosition())) {
            result.addAllElements(VALUE);
            return;
        }
        PsiParameterList parameterList = parameters.getEditor().getUserData(JsonEditor.DEBUG_POWER_EDIT_CONTENT);
        if (parameterList != null && parameterList.getParametersCount() > 0) {
            for (PsiParameter parameter : parameterList.getParameters()) {
                result.addAllElements(KEY);
                result.addElement(LookupElementBuilder.create(parameter.getName()).withTypeText(parameter.getType().getPresentableText()));
            }
        }
    }
}
