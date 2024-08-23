package io.github.future0923.debug.power.idea.ui.tree;

import com.intellij.openapi.project.Project;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import io.github.future0923.debug.power.common.dto.RunResultDTO;
import io.github.future0923.debug.power.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.power.idea.ui.tree.node.EmptyTreeNode;
import io.github.future0923.debug.power.idea.ui.tree.node.ExpandTreeNode;
import io.github.future0923.debug.power.idea.ui.tree.node.ResultTreeNode;
import io.github.future0923.debug.power.idea.ui.tree.node.TreeNode;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.util.List;

/**
 * @author future0923
 */
public class ResultTreePanel extends JBScrollPane {

    private final Tree tree;

    public ResultTreePanel(Project project) {
        this(project, null);
    }

    public ResultTreePanel(Project project, ResultTreeNode root) {
        this.tree = new SimpleTree();
        // 可以拖动的Tree SimpleDnDAwareTree
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setBorder(new CustomLineBorder(JBUI.insetsTop(1)));
        tree.setCellRenderer(new ResultCellRenderer());
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {

            // 展开
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                TreeNode node = (TreeNode) event.getPath().getLastPathComponent();
                if (node.getChildCount() == 1 && node.getFirstChild() instanceof EmptyTreeNode) {
                    node.removeAllChildren();
                    List<RunResultDTO> runResultDTOList = HttpClientUtils.resultDetail(project, node.getUserObject().getFiledOffset());
                    for (RunResultDTO runResultDTO : runResultDTOList) {
                        node.add(new ExpandTreeNode(runResultDTO, runResultDTO.getLeaf()));
                    }
                    ((DefaultTreeModel) tree.getModel()).reload(node);
                }
            }

            // 折叠
            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }
        });
        if (root != null) {
            setRoot(root);
        }
    }

    public void setRoot(ResultTreeNode root) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        tree.setCellRenderer(new ResultCellRenderer());
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        model.setRoot(root);
        ((DefaultTreeModel) tree.getModel()).setRoot(root);
        // 获取根节点的TreePath
        TreePath treePath = new TreePath(tree.getModel().getRoot());
        // 折叠根节点
        tree.collapsePath(treePath);
        this.setViewportView(tree);
    }
}
