package io.github.future0923.debug.tools.idea.ui.tree.node;

import io.github.future0923.debug.tools.common.dto.RunResultDTO;

/**
 * @author future0923
 */
public class ResultDebugTreeNode extends TreeNode<RunResultDTO> {

    public ResultDebugTreeNode(RunResultDTO runResultDTO) {
        this(runResultDTO, runResultDTO.getLeaf());
    }

    public ResultDebugTreeNode(RunResultDTO runResultDTO, boolean leaf) {
        super(runResultDTO);
        if (!leaf) {
            add(new EmptyTreeNode());
        }
    }
}
