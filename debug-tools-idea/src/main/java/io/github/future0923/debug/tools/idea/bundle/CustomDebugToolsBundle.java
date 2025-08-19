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
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.setting.LanguageSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Custom bundle that supports independent language settings.
 *
 * @author future0923
 */
public class CustomDebugToolsBundle extends DynamicBundle {
    
    public static final String BUNDLE = "messages.DebugToolsBundle";
    private static final CustomDebugToolsBundle INSTANCE = new CustomDebugToolsBundle();
    
    private CustomDebugToolsBundle() {
        super(BUNDLE);
    }
    
    @NotNull
    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        LanguageSetting languageSetting = getLanguageSetting();
        if (languageSetting != LanguageSetting.IDE) {
            Locale locale = languageSetting == LanguageSetting.ENGLISH ? Locale.ENGLISH : Locale.CHINESE;
            ClassLoader classLoader = CustomDebugToolsBundle.class.getClassLoader();
            ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE, locale, classLoader);
            return AbstractBundle.message(resourceBundle, key, params);
        }
        return INSTANCE.getMessage(key, params);
    }
    
    @Nullable
    private static DebugToolsSettingState getSettingState() {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return null;
        }
        
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        if (projects.length > 0) {
            return DebugToolsSettingState.getInstance(projects[0]);
        }
        return null;
    }
    
    private static LanguageSetting getLanguageSetting() {
        DebugToolsSettingState settingState = getSettingState();
        if (settingState != null) {
            return settingState.getLanguageSetting();
        }
        return LanguageSetting.IDE;
    }
}