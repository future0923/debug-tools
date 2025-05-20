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
package io.github.future0923.debug.tools.idea.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.codeinsight.JsonCompletionContributor;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import io.github.future0923.debug.tools.common.enums.RunContentType;
import io.github.future0923.debug.tools.idea.ui.main.MainJsonEditor;
import io.github.future0923.debug.tools.idea.utils.DebugToolsJsonElementUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author future0923
 */
public class DebugToolsJsonEditorCompletionContributor extends JsonCompletionContributor {

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
        if (!parameters.getOriginalFile().getName().equals(MainJsonEditor.FILE_NAME)) {
            return;
        }
        // 非key不做处理
        if (!DebugToolsJsonElementUtil.isJsonKey(parameters.getPosition())) {
            result.addAllElements(VALUE);
            return;
        }
        PsiParameterList parameterList = parameters.getEditor().getUserData(MainJsonEditor.DEBUG_POWER_EDIT_CONTENT);
        if (parameterList != null && parameterList.getParametersCount() > 0) {
            for (PsiParameter parameter : parameterList.getParameters()) {
                result.addAllElements(KEY);
                result.addElement(LookupElementBuilder.create(parameter.getName()).withTypeText(parameter.getType().getPresentableText()));
            }
        }
    }
}
