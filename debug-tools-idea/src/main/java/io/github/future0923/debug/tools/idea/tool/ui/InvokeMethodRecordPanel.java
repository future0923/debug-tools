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
package io.github.future0923.debug.tools.idea.tool.ui;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.speedSearch.ListWithFilter;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.StatusText;
import io.github.future0923.debug.tools.base.hutool.core.map.MapUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.utils.DebugToolsDigestUtil;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.model.InvokeMethodRecordDTO;
import io.github.future0923.debug.tools.idea.model.RunStatus;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.ui.dialog.JsonDialogWrapper;
import io.github.future0923.debug.tools.idea.ui.main.InvokeMethodRecordDialog;
import io.github.future0923.debug.tools.idea.utils.DebugToolsActionUtil;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIdeaClassUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author future0923
 */
public class InvokeMethodRecordPanel extends JPanel {

    private final Project project;

    private final BoundedUniqueListModel<InvokeMethodRecordDTO> model;

    private final JBList<InvokeMethodRecordDTO> list;

    private final JBLabel title;

    private AnAction gotoMethodSourceAction;

    private AnAction showCallJsonAction;

    private AnAction showParamJsonAction;

    private AnAction quickDebugAction;

    private AnAction reExecuteAction;

    private AnAction reExecuteDefaultAction;

    private AnAction removeAction;

    public InvokeMethodRecordPanel(Project project) {
        this.project = project;
        setLayout(new BorderLayout());
        title = new JBLabel(DebugToolsBundle.message("invoke.method.record"));
        title.setBorder(JBUI.Borders.empty(5));
        add(title, BorderLayout.NORTH);
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        if (MapUtil.isNotEmpty(settingState.getInvokeMethodRecordMap())) {
            this.model = new BoundedUniqueListModel<>(settingState.getInvokeMethodRecordMap());
        } else {
            this.model = new BoundedUniqueListModel<>();
        }
        this.list = new JBList<>(model);
        JBScrollPane sp = new JBScrollPane(list);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(new CustomLineBorder(JBUI.insetsTop(1)));
        add(ListWithFilter.wrap(list, sp, Object::toString), BorderLayout.CENTER);
        list.setCellRenderer(new ColoredListCellRenderer<>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends InvokeMethodRecordDTO> jList,
                                                 InvokeMethodRecordDTO value, int index,
                                                 boolean selected, boolean hasFocus) {
                if (RunStatus.RUNNING.equals(value.getRunStatus())) {
                    setIcon(DebugToolsIcons.Status.Running);
                } else if (RunStatus.SUCCESS.equals(value.getRunStatus())) {
                    setIcon(DebugToolsIcons.Status.Ok);
                } else if (RunStatus.FAILED.equals(value.getRunStatus())) {
                    setIcon(DebugToolsIcons.Status.Fail);
                }
                if (value.getDuration() != null) {
                    if (value.getDuration()  > 100) {
                        if (value.getDuration() > 10000) {
                            if (value.getDuration() > 300000) {
                                append("[" + value.getDuration() / 60000 + "] min ", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.RED));
                            } else {
                                append("[" + value.getDuration() / 1000 + "] s ", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.RED));
                            }
                        } else {
                            append("[" + value.getDuration() + "] ms ", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.RED));
                        }
                    } else {
                        append("[" + value.getDuration() + "] ms ", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GREEN));
                    }
                }
                append(value.getClassSimpleName() + "#" + value.getMethodSignature(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
                append(" " + value.getRunTime(), SimpleTextAttributes.GRAY_ATTRIBUTES);
            }
        });
        list.getEmptyText().setText(StatusText.getDefaultEmptyText());
        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    int index = list.locationToIndex(e.getPoint());
                    if (index > -1) {
                        list.setSelectedIndex(index);
                    }
                } else {
                    if (e.getClickCount() == 2) {
                        quickDebugPanel();
                    }
                }
            }
        });
        initAction();
    }

    private void initAction() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        // 跳转方法源代码
        gotoMethodSourceAction = new AnAction(DebugToolsBundle.message("goto.method.source")) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                gotoMethodSource();
            }
        };
        actionGroup.add(gotoMethodSourceAction);

        // 运行参数
        String showCallJson = DebugToolsBundle.message("show.call.json");
        showCallJsonAction = new AnAction(showCallJson) {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                InvokeMethodRecordDTO selected = getSelected();
                if (selected == null) {
                    Messages.showErrorDialog(DebugToolsBundle.message("error.get.selected.value"), DebugToolsBundle.message("dialog.title.execution.failed"));
                    return;
                }
                ApplicationManager.getApplication().invokeLater(() -> new JsonDialogWrapper(project, showCallJson, selected.getRunDTO()).show());
            }
        };
        actionGroup.add(showCallJsonAction);

        // 传入参数
        String showParamJson = DebugToolsBundle.message("show.param.json");
        showParamJsonAction = new AnAction(showParamJson) {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                InvokeMethodRecordDTO selected = getSelected();
                if (selected == null) {
                    Messages.showErrorDialog(DebugToolsBundle.message("error.get.selected.value"), DebugToolsBundle.message("dialog.title.execution.failed"));
                    return;
                }
                ApplicationManager.getApplication().invokeLater(() -> new JsonDialogWrapper(project, showParamJson, selected.getMethodParamJson()).show());
            }
        };
        actionGroup.add(showParamJsonAction);

        // 唤醒面板
        quickDebugAction = new AnAction(DebugToolsBundle.message("action.quick.debug.text"), DebugToolsBundle.message("action.quick.debug.description"), DebugToolsIcons.Request) {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                quickDebugPanel();
            }
        };
        actionGroup.add(quickDebugAction);

        // 执行上次
        reExecuteAction = new AnAction(DebugToolsBundle.message("action.rerun.text")) {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                InvokeMethodRecordDTO selected = getSelected();
                if (selected == null) {
                    Messages.showErrorDialog(DebugToolsBundle.message("error.get.selected.value"), DebugToolsBundle.message("dialog.title.execution.failed"));
                    return;
                }
                ApplicationManager.getApplication().invokeLater(() -> DebugToolsActionUtil.executeLast(project, selected.getRunDTO()));
            }
        };
        actionGroup.add(reExecuteAction);

        // 执行上次
        reExecuteDefaultAction = new AnAction(DebugToolsBundle.message("action.rerun.with.default.classloader.text")) {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                InvokeMethodRecordDTO selected = getSelected();
                if (selected == null) {
                    Messages.showErrorDialog(DebugToolsBundle.message("error.get.selected.value"), DebugToolsBundle.message("dialog.title.execution.failed"));
                    return;
                }
                ApplicationManager.getApplication().invokeLater(() -> DebugToolsActionUtil.executeLastWithDefaultClassLoader(project, selected.getRunDTO()));
            }
        };
        actionGroup.add(reExecuteDefaultAction);

        // 移除
        removeAction = new AnAction(DebugToolsBundle.message("action.remove")) {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                InvokeMethodRecordDTO selected = getSelected();
                if (selected == null) {
                    Messages.showErrorDialog(DebugToolsBundle.message("error.get.selected.value"), DebugToolsBundle.message("dialog.title.execution.failed"));
                    return;
                }
                ApplicationManager.getApplication().invokeLater(() -> {
                    model.removeByKey(selected.getIdentity());
                    DebugToolsSettingState.getInstance(project).getInvokeMethodRecordMap().remove(selected.getIdentity());
                });
            }
        };
        actionGroup.add(removeAction);

        // 安装 popup
        PopupHandler.installPopupMenu(
                list,
                actionGroup,
                ActionPlaces.POPUP
        );
    }

    private void quickDebugPanel() {
        InvokeMethodRecordDTO selected = getSelected();
        if (selected == null) {
            Messages.showErrorDialog(DebugToolsBundle.message("error.get.selected.value"), DebugToolsBundle.message("dialog.title.execution.failed"));
            return;
        }
        if (DebugToolsActionUtil.checkAttachSocketError(project)) {
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> new InvokeMethodRecordDialog(project, selected).show());
    }

    private void gotoMethodSource() {
        InvokeMethodRecordDTO selected = getSelected();
        if (selected == null) {
            Messages.showErrorDialog(DebugToolsBundle.message("error.get.selected.value"), DebugToolsBundle.message("dialog.title.execution.failed"));
            return;
        }
        gotoMethodSource(selected);
    }

    private void gotoMethodSource(InvokeMethodRecordDTO selected) {
        ReadAction.nonBlocking(() -> {
            JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            return facade.findClass(selected.getClassName(), scope);

        }).finishOnUiThread(ModalityState.any(), psiClass -> {
            if (psiClass != null) {
                PsiMethod psiMethod = DebugToolsIdeaClassUtil.findMethod(psiClass, selected.getMethodName(), selected.getMethodSignature());
                if (psiMethod != null) {
                    NavigationUtil.activateFileWithPsiElement(psiMethod);
                }
            }
        }).submit(AppExecutorUtil.getAppExecutorService());
    }

    /**
     * 追加数据
     */
    public void addItem(@NotNull InvokeMethodRecordDTO item) {
        if (StrUtil.isBlank(item.getIdentity())) {
            item.setIdentity(DebugToolsDigestUtil.md5(item.getRunDTO()));
        }
        model.addFirst(item, item.getIdentity());
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        settingState.getInvokeMethodRecordMap().put(item.getIdentity(), item);
    }

    public void refreshItem(String identity, RunStatus runStatus, Long duration) {
        model.mutateInPlace(identity, item -> {
            item.setRunStatus(runStatus);
            item.setDuration(duration);
        }, list);
    }

    /**
     * 获取当前选中项
     */
    public @Nullable InvokeMethodRecordDTO getSelected() {
        return list.getSelectedValue();
    }

    public void refresh() {
        title.setText(DebugToolsBundle.message("invoke.method.record"));
        if (gotoMethodSourceAction != null) {
            gotoMethodSourceAction.getTemplatePresentation().setText(DebugToolsBundle.message("goto.method.source"));
        }
        if (showCallJsonAction != null) {
            showCallJsonAction.getTemplatePresentation().setText(DebugToolsBundle.message("show.call.json"));
        }
        if (showParamJsonAction != null) {
            showParamJsonAction.getTemplatePresentation().setText(DebugToolsBundle.message("show.param.json"));
        }
        if (quickDebugAction != null) {
            quickDebugAction.getTemplatePresentation().setText(DebugToolsBundle.message("action.quick.debug.text"));
        }
        if (reExecuteAction != null) {
            reExecuteAction.getTemplatePresentation().setText(DebugToolsBundle.message("action.rerun.text"));
        }
        if (reExecuteDefaultAction != null) {
            reExecuteDefaultAction.getTemplatePresentation().setText(DebugToolsBundle.message("action.rerun.with.default.classloader.text"));
        }
        if (removeAction != null) {
            removeAction.getTemplatePresentation().setText(DebugToolsBundle.message("action.remove"));
        }
    }
}
