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

import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import lombok.Getter;

/**
 * Language setting for the plugin.
 *
 * @author future0923
 */
@Getter
public enum LanguageSetting {
    
    /**
     * Follow the IDE's language setting
     */
    IDE("setting.language.ide", ""),
    
    /**
     * English
     */
    ENGLISH("setting.language.english", "en"),
    
    /**
     * Chinese
     */
    CHINESE("setting.language.chinese", "zh");
    
    private final String bundleKey;
    private final String locale;
    
    LanguageSetting(String bundleKey, String locale) {
        this.bundleKey = bundleKey;
        this.locale = locale;
    }
    
    public String getDisplayName() {
        return DebugToolsBundle.message(bundleKey);
    }
    
    public static LanguageSetting fromLocale(String locale) {
        if (locale == null) {
            return IDE;
        }
        for (LanguageSetting setting : values()) {
            if (locale.equals(setting.getLocale())) {
                return setting;
            }
        }
        return IDE;
    }
}