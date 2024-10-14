package io.github.future0923.debug.tools.idea.ui.main;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.tools.idea.context.MethodDataContext;
import io.github.future0923.debug.tools.idea.listener.data.MulticasterEventPublisher;
import io.github.future0923.debug.tools.idea.listener.data.impl.ConvertDataListener;
import io.github.future0923.debug.tools.idea.listener.data.impl.PrettyDataListener;
import io.github.future0923.debug.tools.idea.listener.data.impl.SimpleDataListener;
import io.github.future0923.debug.tools.idea.model.ParamCache;
import io.github.future0923.debug.tools.idea.utils.DebugToolsUIHelper;
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
    private final ComboBox<AllClassLoaderRes.Item> classLoaderComboBox = new ComboBox<>(600);

    private final JButton refreshButton = new JButton("Refresh");

    private final JBTextField classNameField = new JBTextField();

    private final JBTextField methodNameField = new JBTextField();

    private final Map<JBTextField, JBTextField> headerItemMap = new HashMap<>();

    @Getter
    private final JBTextField xxlJobParamField = new JBTextField();

    private final MainToolBar toolBar;

    @Getter
    private final MainJsonEditor editor;

    public MainPanel(Project project, MethodDataContext methodDataContext) {
        super(new GridBagLayout());
        setPreferredSize(new JBDimension(800, 600));
        this.project = project;
        this.methodDataContext = methodDataContext;
        // 当前类和方法
        PsiMethod psiMethod = methodDataContext.getPsiMethod();
        PsiClass psiClass = methodDataContext.getPsiClass();
        if (psiClass != null && psiMethod != null) {
            classNameField.setText(psiClass.getQualifiedName());
            methodNameField.setText(psiMethod.getName());
        }
        if (StringUtils.isNotBlank(methodDataContext.getCache().getXxlJobParam())) {
            xxlJobParamField.setText(methodDataContext.getCache().getXxlJobParam());
        }
        MulticasterEventPublisher publisher = new MulticasterEventPublisher();
        // 工具栏
        this.toolBar = new MainToolBar(publisher);
        // json编辑器
        this.editor = new MainJsonEditor(methodDataContext.getCache().formatContent(), methodDataContext.getParamList(), project);
        publisher.addListener(new SimpleDataListener(editor));
        publisher.addListener(new PrettyDataListener(editor));
        publisher.addListener(new ConvertDataListener(project, editor));
        initLayout();
    }

    private void initLayout() {
        JPanel classLoaderJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        classLoaderComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                AllClassLoaderRes.Item item = (AllClassLoaderRes.Item) value;
                if (item == null) {
                    return new JLabel();
                }
                String classLoader = item.getName();
                JLabel jLabel = (JLabel) super.getListCellRendererComponent(list, classLoader, index, isSelected, cellHasFocus);
                jLabel.setText(classLoader + "@" + item.getIdentity());
                return jLabel;
            }
        });
        getAllClassLoader(true);
        refreshButton.addActionListener( e -> {
            classLoaderComboBox.removeAllItems();
            getAllClassLoader(false);
        });
        classLoaderJPanel.add(classLoaderComboBox);
        classLoaderJPanel.add(refreshButton);
        JPanel headerButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        JPanel jPanel = formBuilder
                .addLabeledComponent(
                        new JBLabel("Class loader:"),
                        classLoaderJPanel
                )
                .addLabeledComponent(
                        new JBLabel("Current class:"),
                        classNameField
                )
                .addLabeledComponent(
                        new JBLabel("Current method:"),
                        methodNameField
                )
                .addLabeledComponent(
                        new JBLabel("Xxl-job param:"),
                        xxlJobParamField
                )
                .addLabeledComponent(
                        new JBLabel("Header:"),
                        headerButtonPanel
                )
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        JButton addHeaderButton = new JButton("Add");
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

    private void getAllClassLoader(boolean cache) {
        AllClassLoaderRes allClassLoaderRes = HttpClientUtils.allClassLoader(project, cache);
        AllClassLoaderRes.Item defaultClassLoader = null;
        for (AllClassLoaderRes.Item item : allClassLoaderRes.getItemList()) {
            if (item.getIdentity().equals(allClassLoaderRes.getDefaultIdentity())) {
                defaultClassLoader = item;
            }
            classLoaderComboBox.addItem(item);
        }
        if (defaultClassLoader != null) {
            classLoaderComboBox.setSelectedItem(defaultClassLoader);
        }
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
