package io.github.future0923.debug.tools.idea.ui.tree.node;

import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.trace.MethodTreeNode;

/**
 * @author future0923
 */
public class ResultTraceTreeNode extends TreeNode<MethodTreeNode> {

    public ResultTraceTreeNode(MethodTreeNode runResultDTO) {
        super(runResultDTO);
        if (CollUtil.isNotEmpty(runResultDTO.getChildren())) {
            add(new EmptyTreeNode());
        }
    }
}
