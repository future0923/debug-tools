package io.github.future0923.debug.tools.idea.action;

import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.ide.util.gotoByName.DefaultChooseByNameItemProvider;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import io.github.future0923.debug.tools.idea.httpmethod.HttpMethodContributor;
import io.github.future0923.debug.tools.idea.httpmethod.HttpMethodChooseByNamePopup;
import io.github.future0923.debug.tools.idea.httpmethod.HttpMethodFilteringGotoByModel;
import io.github.future0923.debug.tools.idea.httpmethod.beans.HttpMethodItem;
import io.github.future0923.debug.tools.idea.httpmethod.enums.HttpMethod;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 搜索http请求
 *
 * @author future0923
 */
public class HttpMethodSearchGotoAction extends GotoActionBase {

    public HttpMethodSearchGotoAction() {
        getTemplatePresentation().setText("Search Http Method");
        getTemplatePresentation().setIcon(DebugToolsIcons.Search);
    }

    @Override
    protected void gotoActionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        ChooseByNameContributor[] contributors = {new HttpMethodContributor(e.getData(LangDataKeys.MODULE))};
        HttpMethodFilteringGotoByModel model = new HttpMethodFilteringGotoByModel(project, contributors);
        GotoActionCallback<HttpMethod> callback = new GotoActionCallback<>() {

            @Override
            public void elementChosen(ChooseByNamePopup popup, Object element) {
                if (element instanceof HttpMethodItem apiItem) {
                    if (apiItem.canNavigate()) {
                        apiItem.navigate(true);
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
                HttpMethodChooseByNamePopup.createPopup(
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
