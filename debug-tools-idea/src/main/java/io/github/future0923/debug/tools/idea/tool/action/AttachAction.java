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
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindow;
import io.github.future0923.debug.tools.idea.tool.ui.AttachServerPopup;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;

import java.awt.*;

/**
 * @author future0923
 */
public class AttachAction extends BaseToolAction {

    public AttachAction() {
        getTemplatePresentation().setText(DebugToolsBundle.message("action.attach"));
        getTemplatePresentation().setIcon(DebugToolsIcons.Add);
    }

    @Override
    protected void doActionPerformed(Project project, DebugToolsToolWindow toolWindow) {
        AttachServerPopup popup = new AttachServerPopup(project);
        Point location = MouseInfo.getPointerInfo().getLocation();
        location.x += 10;
        location.y -= 10;
        popup.show(location);
    }
}
