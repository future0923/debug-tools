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
package io.github.future0923.debug.tools.idea.action;

import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.ide.util.gotoByName.DefaultChooseByNameItemProvider;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import io.github.future0923.debug.tools.idea.search.HttpUrlChooseByNamePopup;
import io.github.future0923.debug.tools.idea.search.HttpUrlContributor;
import io.github.future0923.debug.tools.idea.search.HttpUrlFilteringGotoByModel;
import io.github.future0923.debug.tools.idea.search.beans.HttpUrlItem;
import io.github.future0923.debug.tools.idea.search.enums.HttpMethod;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 搜索http请求
 *
 * @author future0923
 */
public class HttpUrlSearchGotoAction extends GotoActionBase {

    public HttpUrlSearchGotoAction() {
        getTemplatePresentation().setText("Search Http Url");
        getTemplatePresentation().setIcon(DebugToolsIcons.Search);
    }

    @Override
    protected void gotoActionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        ChooseByNameContributor[] contributors = {new HttpUrlContributor()};
        HttpUrlFilteringGotoByModel model = new HttpUrlFilteringGotoByModel(project, contributors);
        GotoActionCallback<HttpMethod> callback = new GotoActionCallback<>() {

            @Override
            public void elementChosen(ChooseByNamePopup popup, Object element) {
                if (element instanceof HttpUrlItem httpUrlItem) {
                    if (httpUrlItem.canNavigate()) {
                        httpUrlItem.navigate(true);
                    }
                }
            }
        };
        showNavigationPopup(
                e, model, callback,
                "Request Mapping Url matching pattern",
                true,
                false,
                new DefaultChooseByNameItemProvider(getPsiContext(e))
        );
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected <T> void showNavigationPopup(@NotNull AnActionEvent e,
                                           @NotNull ChooseByNameModel model,
                                           final GotoActionCallback<T> callback,
                                           @Nullable final String findUsagesTitle,
                                           boolean useSelectionFromEditor,
                                           final boolean allowMultipleSelection,
                                           final ChooseByNameItemProvider itemProvider) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        boolean mayRequestOpenInCurrentWindow = model.willOpenEditor() && FileEditorManagerEx.getInstanceEx(project).hasSplitOrUndockedWindows();
        Pair<String, Integer> start = getInitialText(useSelectionFromEditor, e);
        showNavigationPopup(
                callback,
                findUsagesTitle,
                HttpUrlChooseByNamePopup.createPopup(
                        project,
                        model,
                        itemProvider,
                        start.first,
                        mayRequestOpenInCurrentWindow,
                        start.second
                ),
                allowMultipleSelection
        );
    }
}
