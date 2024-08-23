package io.github.future0923.debug.power.idea.ui.tree.node;

import io.github.future0923.debug.power.common.dto.RunResultDTO;

/**
 * @author future0923
 */
public class ResultTreeNode extends TreeNode {

    public ResultTreeNode(RunResultDTO runResultDTO) {
        this(runResultDTO, runResultDTO.getLeaf());
    }

    public ResultTreeNode(RunResultDTO runResultDTO, boolean leaf) {
        super(runResultDTO);
        if (!leaf) {
            add(new EmptyTreeNode());
        }
    }
}
