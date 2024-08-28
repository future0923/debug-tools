package io.github.future0923.debug.power.idea.ui.tree;

import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import io.github.future0923.debug.power.base.utils.DebugPowerStringUtils;
import io.github.future0923.debug.power.common.dto.RunResultDTO;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;
import io.github.future0923.debug.power.idea.ui.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author future0923
 */
public class ResultCellRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        RunResultDTO runResultDTO = ((TreeNode) value).getUserObject();
        if (RunResultDTO.Type.ROOT.equals(runResultDTO.getType())) {
            setIcon(AllIcons.Debugger.Value);
            append("result", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.ORANGE));
            append(" = ");
        } else if (RunResultDTO.Type.MAP.equals(runResultDTO.getType())) {
            setIcon(AllIcons.Debugger.Value);
            appendClassInfo(runResultDTO.getNameClassName(), runResultDTO.getNameIdentity(), runResultDTO.getName(), runResultDTO.getNameArray());
            append(" ");
            appendValueInfo(runResultDTO.getNameClassName(), runResultDTO.getName(), runResultDTO.getNameChildSize(), runResultDTO.getNameArray());
            append(" -> ");
        } else if (RunResultDTO.Type.MAP_ENTRY.equals(runResultDTO.getType())) {
            setIcon(AllIcons.Debugger.Value);
            append(runResultDTO.getName(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.ORANGE));
            append(" = ");
        } else {
            setIcon(AllIcons.Nodes.Field);
            append(runResultDTO.getName(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.ORANGE));
            append(" = ");
        }
        appendClassInfo(runResultDTO.getValueClassName(), runResultDTO.getValueIdentity(), runResultDTO.getValue(), runResultDTO.getValueArray());
        append(" ");
        appendValueInfo(runResultDTO.getValueClassName(), runResultDTO.getValue(), runResultDTO.getValueChildSize(), runResultDTO.getValueArray());
    }

    private void appendValueInfo(String className, String value, Integer childSize, boolean array) {
        // 有子集，并且不是数组
        if (childSize != null && !array) {
            append("size = " + childSize);
        } else {
            if (value == null) {
                append("null");
            } else {
                Class<?> aClass = getClass(className);
                if (DebugPowerClassUtils.isBasicType(aClass)) {
                    append(value);
                } else if ("java.lang.String".equals(className)) {
                    append("\"" + value + "\"", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GREEN));
                } else {
                    append("\"" + value + "\"");
                }
            }
        }
    }

    private void appendClassInfo(String className, String identity, String value, boolean array) {
        // null不展示
        if (value == null) {
            return;
        }
        // 不是数组，因为数组className格式为"byte[1]"等，肯定不能得到class
        if (!array) {
            // 基本类型不展示
            Class<?> aClass = getClass(className);
            if (aClass != null && aClass.isPrimitive()) {
                return;
            }
        }
        // String不展示
        if ("java.lang.String".equals(className)) {
            return;
        }
        String simpleName = DebugPowerClassUtils.getSimpleName(className);
        if (simpleName == null) {
            simpleName = "Null";
        }
        append("{" + simpleName + "@" + identity + "}", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GRAY));
    }

    private Class<?> getClass(String className) {
        if (DebugPowerStringUtils.isBlank(className)) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
