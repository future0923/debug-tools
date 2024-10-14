package io.github.future0923.debug.tools.idea.ui.tree.node;

import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;

/**
 * @author future0923
 */
public abstract class TreeNode extends PatchedDefaultMutableTreeNode {

    public TreeNode() {
    }

    public TreeNode(Object userObject) {
        super(userObject);
    }

    @Override
    public RunResultDTO getUserObject() {
        return (RunResultDTO) super.getUserObject();
    }
}
