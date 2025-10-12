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
package io.github.future0923.debug.tools.idea.camel;

import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;

/**
 * @author future0923
 */
public class ToggleCamelCaseAction extends TextComponentEditorAction {

    public ToggleCamelCaseAction() {
        super(new BaseEditorActionHandler() {

            /**
             * camelCase
             */
            @Override
            protected boolean useCamelCase() {
                return true;
            }

            /**
             * snake_case
             */
            @Override
            protected boolean useLowerSnakeCase() {
                return true;
            }

            /**
             * CamelCase
             */
            @Override
            protected boolean usePascalCase() {
                return true;
            }

            /**
             * SNAKE_CASE
             */
            @Override
            protected boolean useUpperSnakeCase() {
                return true;
            }

            /**
             * kebab-case
             */
            @Override
            protected boolean useKebabCase() {
                return true;
            }
        });
        getTemplatePresentation().setText(DebugToolsBundle.message("action.convert.toggle"));
    }
}
