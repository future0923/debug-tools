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
package io.github.future0923.debug.tools.idea.ui.tree;

import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.JBColor;
import io.github.future0923.debug.tools.base.trace.MethodTraceType;
import io.github.future0923.debug.tools.base.trace.MethodTreeNode;
import io.github.future0923.debug.tools.idea.ui.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author future0923
 */
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class ResultTraceCellRenderer extends ColoredTreeCellRenderer {

    private static final Color ORANGE = new JBColor(new Color(0x753F3E), new Color(0xE6AE87));

    private static final Color GREEN = new JBColor(new Color(0x377A2A), new Color(0x79A878));

    private static final Color GRAY = new JBColor(new Color(0x818593), new Color(0x707379));

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        MethodTreeNode runResultDTO = ((TreeNode<MethodTreeNode>) value).getUserObject();
        if (runResultDTO == null) {
            return;
        }
        append(runResultDTO.getDuration() + "ms");
        append(" ");
        append(runResultDTO.getTraceType().name());
        if (!runResultDTO.getTraceType().equals(MethodTraceType.SQL)) {
            append(" ");
            append(runResultDTO.getClassSimpleName());
            append("#");
            append(runResultDTO.getMethodName());
        } else {
            append(runResultDTO.getSql());
        }

    }

}
