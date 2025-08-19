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
package io.github.future0923.debug.tools.idea.bundle;

import com.intellij.AbstractBundle;
import com.intellij.DynamicBundle;
import io.github.future0923.debug.tools.idea.setting.DebugToolsGlobalSettingState;
import io.github.future0923.debug.tools.idea.setting.LanguageSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

public class DebugToolsBundle extends DynamicBundle {

    private static final String BUNDLE = "messages.DebugToolsBundle";

    private DebugToolsBundle() {
        super(DebugToolsBundle.BUNDLE);
    }

    @NotNull
    public static String message(@NotNull @PropertyKey(resourceBundle = DebugToolsBundle.BUNDLE) String key, Object... params) {
        LanguageSetting language = DebugToolsGlobalSettingState.getInstance().getLanguage();
        ClassLoader classLoader = DebugToolsBundle.class.getClassLoader();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(DebugToolsBundle.BUNDLE, language.getLocale(), classLoader);
        return AbstractBundle.message(resourceBundle, key, params);
    }
}