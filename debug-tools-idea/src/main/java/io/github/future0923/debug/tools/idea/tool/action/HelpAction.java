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
package io.github.future0923.debug.tools.idea.tool.action;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindow;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;

import javax.swing.*;
import java.awt.*;

/**
 * @author caoayu
 */
public class HelpAction extends BaseToolAction {

    public HelpAction() {
        getTemplatePresentation().setText("Help");
        getTemplatePresentation().setIcon(DebugToolsIcons.Help);
    }

    @Override
    protected void doActionPerformed(Project project, DebugToolsToolWindow toolWindow) {
        try {
            Desktop.getDesktop().browse(new java.net.URI("https://debug-tools.cc/"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "The browser could not be opened automatically. Please open it manually: https://debug-tools.cc/", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 