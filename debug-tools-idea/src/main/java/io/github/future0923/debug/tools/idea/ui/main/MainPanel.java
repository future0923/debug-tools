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
package io.github.future0923.debug.tools.idea.ui.main;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.context.MethodDataContext;
import io.github.future0923.debug.tools.idea.listener.data.MulticasterEventPublisher;
import io.github.future0923.debug.tools.idea.listener.data.impl.ConvertDataListener;
import io.github.future0923.debug.tools.idea.listener.data.impl.PrettyDataListener;
import io.github.future0923.debug.tools.idea.listener.data.impl.SimpleDataListener;
import io.github.future0923.debug.tools.idea.model.ParamCache;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.ui.combobox.ClassLoaderComboBox;
import io.github.future0923.debug.tools.idea.ui.combobox.MethodAroundComboBox;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIdeaClassUtil;
import io.github.future0923.debug.tools.idea.utils.DebugToolsUIHelper;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author future0923
 */
public class MainPanel extends JBPanel<MainPanel> {

    private final Project project;

    private final MethodDataContext methodDataContext;

    @Getter
    private final ClassLoaderComboBox classLoaderComboBox;

    @Getter
    private final MethodAroundComboBox methodAroundComboBox;

    private final JButton refreshButton = new JButton(DebugToolsBundle.message("main.panel.refresh"));

    private final JBTextField classNameField = new JBTextField();

    private final JBTextField methodNameField = new JBTextField();

    @Getter
    private final TraceMethodPanel traceMethodPanel;

    private final Map<JBTextField, JBTextField> headerItemMap = new HashMap<>();

    @Getter
    private final JBTextField xxlJobParamField = new JBTextField();

    private final MainToolBar toolBar;

    @Getter
    private final MainJsonEditor editor;

    public MainPanel(Project project, MethodDataContext methodDataContext) {
        super(new GridBagLayout());
        setPreferredSize(new JBDimension(800, 700));
        this.project = project;
        this.classLoaderComboBox = new ClassLoaderComboBox(project, 600, false);
        this.methodDataContext = methodDataContext;
        // 当前类和方法
        PsiMethod psiMethod = methodDataContext.getPsiMethod();
        PsiClass psiClass = methodDataContext.getPsiClass();
        if (psiClass != null && psiMethod != null) {
            classNameField.setText(DebugToolsIdeaClassUtil.tryInnerClassName(psiClass));
            methodNameField.setText(psiMethod.getName());
        }
        ParamCache paramCache = methodDataContext.getCache();
        if (StringUtils.isNotBlank(paramCache.getXxlJobParam())) {
            xxlJobParamField.setText(paramCache.getXxlJobParam());
        }
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        methodAroundComboBox = new MethodAroundComboBox(project, 370);
        if (StrUtil.isNotBlank(paramCache.getMethodAround())) {
            methodAroundComboBox.setSelected(paramCache.getMethodAround());
        } else if (StrUtil.isNotBlank(settingState.getDefaultMethodAroundName())) {
            methodAroundComboBox.setSelected(settingState.getDefaultMethodAroundName());
        }
        traceMethodPanel = new TraceMethodPanel();
        traceMethodPanel.processDefaultInfo(project, paramCache.getTraceMethodDTO());
        MulticasterEventPublisher publisher = new MulticasterEventPublisher();
        // 工具栏
        this.toolBar = new MainToolBar(publisher, project);
        // json编辑器
        this.editor = new MainJsonEditor(paramCache.formatContent(), methodDataContext.getParamList(), project);
        publisher.addListener(new SimpleDataListener(editor));
        publisher.addListener(new PrettyDataListener(editor));
        publisher.addListener(new ConvertDataListener(project, editor));
        initLayout();
    }

    private void initLayout() {
        JPanel classLoaderJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        getAllClassLoader();
        refreshButton.addActionListener(e -> {
            classLoaderComboBox.removeAllItems();
            getAllClassLoader();
            classLoaderComboBox.setSelectedClassLoader(StateUtils.getProjectDefaultClassLoader(project));
        });
        classLoaderJPanel.add(classLoaderComboBox);
        classLoaderJPanel.add(refreshButton);
        JPanel methodAroundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        methodAroundPanel.add(methodAroundComboBox);
        methodAroundPanel.add(methodAroundComboBox.getMethodAroundPanel());
        JPanel headerButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        JPanel jPanel = formBuilder
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.class.loader")),
                        classLoaderJPanel
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.current.class")),
                        classNameField
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.current.method")),
                        methodNameField
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.xxl.job.param")),
                        xxlJobParamField
                )
                .addLabeledComponent(
                        new JBLabel("Method around:"),
                        methodAroundPanel
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.trace.method")),
                        traceMethodPanel.getComponent()
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.header")),
                        headerButtonPanel
                )
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        JButton addHeaderButton = new JButton(DebugToolsBundle.message("main.panel.add"));
        headerButtonPanel.add(addHeaderButton);
        addHeaderButton.addActionListener(e -> {
            DebugToolsUIHelper.addHeaderLabelItem(jPanel, formBuilder, 150, 400, null, null, headerItemMap);
            DebugToolsUIHelper.refreshUI(formBuilder);
        });
        Optional.of(methodDataContext)
                .map(MethodDataContext::getCache)
                .map(ParamCache::getItemHeaderMap)
                .ifPresent(map -> map.forEach((key, value) -> DebugToolsUIHelper.addHeaderLabelItem(jPanel, formBuilder, 150, 400, key, value, headerItemMap)));
        DebugToolsUIHelper.refreshUI(formBuilder);

        GridBagConstraints gbc = new GridBagConstraints();
        // 将组件的填充方式设置为水平填充。这意味着组件将在水平方向上拉伸以填充其在容器中的可用空间，但不会在垂直方向上拉伸。
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(jPanel, gbc);

        gbc.fill = GridBagConstraints.LINE_START;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(toolBar, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.add(editor, gbc);
    }

    private void getAllClassLoader() {
        classLoaderComboBox.refreshClassLoader(false);
        classLoaderComboBox.setSelectedClassLoader(StateUtils.getProjectDefaultClassLoader(project));
    }

    public Map<String, String> getItemHeaderMap() {
        Map<String, String> headerMap = new HashMap<>(headerItemMap.size());
        headerItemMap.forEach((k, v) -> {
            String key = k.getText();
            if (StringUtils.isNotBlank(key)) {
                headerMap.put(key, v.getText());
            }
        });
        return headerMap;
    }
}
