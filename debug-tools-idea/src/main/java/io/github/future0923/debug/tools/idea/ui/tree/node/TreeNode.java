package io.github.future0923.debug.tools.idea.ui.tree.node;

import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;

/**
 * @author future0923
 */
@SuppressWarnings("unchecked")
public abstract class TreeNode<T> extends PatchedDefaultMutableTreeNode {

    public TreeNode() {
    }

    public TreeNode(Object userObject) {
        super(userObject);
    }

    @Override
    public T getUserObject() {
        return (T) super.getUserObject();
    }
}
