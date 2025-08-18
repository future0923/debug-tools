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

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;
import io.github.future0923.debug.tools.base.trace.MethodTraceType;
import io.github.future0923.debug.tools.base.trace.MethodTreeNode;
import io.github.future0923.debug.tools.idea.ui.dialog.SqlDialogWrapper;
import io.github.future0923.debug.tools.idea.ui.tree.node.EmptyTreeNode;
import io.github.future0923.debug.tools.idea.ui.tree.node.ResultTraceTreeNode;
import io.github.future0923.debug.tools.idea.ui.tree.node.TreeNode;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIdeaClassUtil;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * trace树面板
 *
 * @author future0923
 */
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class ResultTraceTreePanel extends JBScrollPane {

    private final Project project;

    private final Tree tree;

    public ResultTraceTreePanel(Project project) {
        this(project, null);
    }

    public ResultTraceTreePanel(Project project, ResultTraceTreeNode root) {
        this.project = project;
        this.tree = new SimpleTree();
        // 可以拖动的Tree SimpleDnDAwareTree
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setBorder(new CustomLineBorder(JBUI.insetsTop(1)));
        tree.setCellRenderer(new ResultTraceCellRenderer());
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {

            // 展开
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                if (event.getPath().getLastPathComponent() instanceof TreeNode node) {
                    if (node.getChildCount() == 1 && node.getFirstChild() instanceof EmptyTreeNode) {
                        node.removeAllChildren();
                        MethodTreeNode methodTree = (MethodTreeNode) node.getUserObject();
                        List<MethodTreeNode> children = methodTree.getChildren();
                        if (CollUtil.isNotEmpty(children)) {
                            for (MethodTreeNode child : children) {
                                node.add(new ResultTraceTreeNode(child));
                            }
                        }
                        ((DefaultTreeModel) tree.getModel()).reload(node);
                    }
                }
            }

            // 折叠
            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }
        });
        JMenuItem gotoMethodSource = new JMenuItem("Goto method source");
        JMenuItem showSqlDetail = new JMenuItem("Show sql detail");
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    TreePath path = tree.getPathForRow(row);
                    if (path != null) {
                        TreeNode<MethodTreeNode> selectedNode = (TreeNode<MethodTreeNode>) path.getLastPathComponent();
                        MethodTreeNode treeNode = selectedNode.getUserObject();
                        if (treeNode == null) {
                            return;
                        }
                        JPopupMenu popupMenu = new JPopupMenu();
                        if (MethodTraceType.SQL.equals(treeNode.getTraceType())) {
                            popupMenu.add(showSqlDetail);
                        } else {
                            popupMenu.add(gotoMethodSource);
                        }
                        // 显示右键菜单
                        popupMenu.show(tree, e.getX(), e.getY());
                    }
                }
                // 双击
                if (ObjectUtil.equal(e.getClickCount(), 2)) {
                    showSqlDetail();
                }
            }
        });
        gotoMethodSource.addActionListener(e -> {
            TreePath selectedPath = tree.getSelectionPath();
            if (selectedPath != null) {
                TreeNode<MethodTreeNode> selectedNode = (TreeNode<MethodTreeNode>) selectedPath.getLastPathComponent();
                MethodTreeNode treeNode = selectedNode.getUserObject();
                if (treeNode == null) {
                    return;
                }
                if (MethodTraceType.SQL.equals(treeNode.getTraceType())) {
                    return;
                }
                JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
                GlobalSearchScope scope = GlobalSearchScope.allScope(project);
                PsiClass psiClass = facade.findClass(treeNode.getClassName(), scope);
                if (psiClass != null) {
                    PsiMethod psiMethod = DebugToolsIdeaClassUtil.findMethod(psiClass, treeNode.getMethodName(), treeNode.getMethodSignature());
                    if (psiMethod != null) {
                        NavigationUtil.activateFileWithPsiElement(psiMethod);
                    }
                }
            }
        });
        showSqlDetail.addActionListener(e -> showSqlDetail());
        if (root != null) {
            setRoot(root);
        }
    }

    public void showSqlDetail() {
        TreePath selectedPath = tree.getSelectionPath();
        if (selectedPath != null) {
            TreeNode<MethodTreeNode> selectedNode = (TreeNode<MethodTreeNode>) selectedPath.getLastPathComponent();
            MethodTreeNode treeNode = selectedNode.getUserObject();
            if (treeNode == null) {
                return;
            }
            if (!MethodTraceType.SQL.equals(treeNode.getTraceType())) {
                return;
            }
            new SqlDialogWrapper(project, treeNode.getSql()).show();
        }
    }

    public void setRoot(ResultTraceTreeNode root) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
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
