package io.github.future0923.debug.tools.idea.search;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 集成到 Search Anywhere (Double Shift) - 老版本 SDK 适配
 * @author caoayu
 */
public class HttpUrlSearchContributorFactory implements SearchEverywhereContributorFactory {
    @Override
    public @NotNull SearchEverywhereContributor createContributor(@NotNull AnActionEvent anActionEvent) {
        return new HttpUrlSearchEverywhereContributor(anActionEvent);
    }
}
