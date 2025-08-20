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
package io.github.future0923.debug.tools.idea.setting;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Service
@State(name = "DebugToolsGlobalSettingState", storages = @Storage("DebugToolsGlobalSettingState.xml"))
public final class DebugToolsGlobalSettingState implements PersistentStateComponent<DebugToolsGlobalSettingState> {

    /**
     * 插件语言设置
     */
    private LanguageSetting language = LanguageSetting.IDE;

    @Override
    public @NotNull DebugToolsGlobalSettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DebugToolsGlobalSettingState debugToolsGlobalSettingState) {
        XmlSerializerUtil.copyBean(debugToolsGlobalSettingState, this);
    }

    public static DebugToolsGlobalSettingState getInstance() {
        return ApplicationManager.getApplication().getService(DebugToolsGlobalSettingState.class);
    }
}
