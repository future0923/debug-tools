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
package io.github.future0923.debug.tools.idea.search;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.gotoByName.CustomMatcherModel;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import io.github.future0923.debug.tools.idea.search.beans.HttpUrlItem;
import io.github.future0923.debug.tools.idea.search.enums.HttpMethod;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author future0923
 */
public class HttpUrlFilteringGotoByModel extends FilteringGotoByModel<HttpMethod> implements DumbAware, CustomMatcherModel {

    public HttpUrlFilteringGotoByModel(@NotNull Project project, ChooseByNameContributor @NotNull [] contributors) {
        super(project, contributors);
    }

    @Override
    protected @Nullable HttpMethod filterValueFor(NavigationItem item) {
        if (item instanceof HttpUrlItem httpUrlItem) {
            return httpUrlItem.getMethod();
        }
        return null;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) String getPromptText() {
        return "Enter http url";
    }

    @Override
    public @NotNull @NlsContexts.Label String getNotInMessage() {
        return IdeBundle.message("label.no.matches.found", getProject().getName());
    }

    @Override
    public @NotNull @NlsContexts.Label String getNotFoundMessage() {
        return IdeBundle.message("label.no.matches.found");
    }

    @Override
    public @Nullable @NlsContexts.Label String getCheckBoxName() {
        return null;
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return true;
    }

    @Override
    public void saveInitialCheckBoxState(boolean state) {

    }

    @Override
    public String @NotNull [] getSeparators() {
        return new String[]{"/", "?"};
    }

    @Override
    public @Nullable String getFullName(@NotNull Object element) {
        return getElementName(element);
    }

    @Override
    public boolean willOpenEditor() {
        return true;
    }

    @Override
    public boolean matches(@NotNull String popupItem, @NotNull String userPattern) {
        return true;
    }
}
