package io.github.future0923.debug.power.idea.ui.tree;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import io.github.future0923.debug.power.common.dto.RunResultDTO;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;
import io.github.future0923.debug.power.idea.ui.tree.node.ExpandTreeNode;
import io.github.future0923.debug.power.idea.ui.tree.node.ResultTreeNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author future0923
 */
public class ResultCellRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof ResultTreeNode treeNode) {
            setIcon(AllIcons.Debugger.Value);
            RunResultDTO runResultDTO = treeNode.getUserObject();
            append(runResultDTO.getName(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.ORANGE));
            append(" = ");
            append("{" + DebugPowerClassUtils.getSimpleName(runResultDTO.getClassName()) + "@" + runResultDTO.getIdentity() +"}", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GRAY));
            append(" ");
            Class<?> simpleType = isSimpleType(runResultDTO.getClassName());
            if (simpleType != null && !CharSequence.class.isAssignableFrom(simpleType)) {
                append(Convert.toStr(runResultDTO.getValue()));
            } else {
                append("\"" + runResultDTO.getValue() + "\"");
            }
        } else if (value instanceof ExpandTreeNode treeNode) {
            setIcon(AllIcons.Nodes.Field);
            RunResultDTO runResultDTO = treeNode.getUserObject();
            append(runResultDTO.getName(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.ORANGE));
            append(" = ");
            append("{" + DebugPowerClassUtils.getSimpleName(runResultDTO.getClassName()) + "@" + runResultDTO.getIdentity() +"}", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GRAY));
            append(" ");
            Class<?> simpleType = isSimpleType(runResultDTO.getClassName());
            if (simpleType != null) {
                if (CharSequence.class.isAssignableFrom(simpleType)) {
                    append("\"" + runResultDTO.getValue() + "\"", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GREEN));
                } else {
                    append(Convert.toStr(runResultDTO.getValue()));
                }
            } else {
                append("\"" + runResultDTO.getValue() + "\"");
            }
        }
    }

    private Class<?> isSimpleType(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return ClassUtil.isSimpleValueType(clazz) ? clazz : null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
