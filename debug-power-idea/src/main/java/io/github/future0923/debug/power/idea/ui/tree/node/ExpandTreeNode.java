package io.github.future0923.debug.power.idea.ui.tree.node;

import io.github.future0923.debug.power.common.dto.RunResultDTO;

/**
 * @author future0923
 */
public class ExpandTreeNode extends TreeNode {

    public ExpandTreeNode(RunResultDTO runResultDTO) {
        this(runResultDTO, runResultDTO.getLeaf());
    }

    public ExpandTreeNode(RunResultDTO runResultDTO, boolean leaf) {
        super(runResultDTO);
        if (!leaf) {
            add(new EmptyTreeNode());
        }
    }
}
