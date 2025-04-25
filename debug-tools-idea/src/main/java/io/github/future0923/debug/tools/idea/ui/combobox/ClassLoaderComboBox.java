package io.github.future0923.debug.tools.idea.ui.combobox;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.tools.idea.utils.StateUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @author future0923
 */
public class ClassLoaderComboBox extends ComboBox<AllClassLoaderRes.Item> {

    private final Project project;

    private volatile boolean init = false;

    public ClassLoaderComboBox(Project project) {
        this(project, -1, true);
    }

    public ClassLoaderComboBox(Project project, int width, boolean isDefault) {
        super(width);
        this.project = project;
        setRenderer(new DefaultListCellRenderer() {
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
        if (isDefault) {
            addActionListener(e-> {
                AllClassLoaderRes.Item selectedItem = (AllClassLoaderRes.Item) getSelectedItem();
                if (selectedItem != null) {
                    StateUtils.setProjectDefaultClassLoader(project, selectedItem);
                }
            });
        }
    }

    public void getAllClassLoader(boolean cache) {
        removeAllItems();
        AllClassLoaderRes allClassLoaderRes;
        try {
            allClassLoaderRes = HttpClientUtils.allClassLoader(project, cache);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        AllClassLoaderRes.Item defaultClassLoader = null;
        for (AllClassLoaderRes.Item item : allClassLoaderRes.getItemList()) {
            if (item.getIdentity().equals(allClassLoaderRes.getDefaultIdentity())) {
                defaultClassLoader = item;
            }
            addItem(item);
        }
        if (defaultClassLoader != null) {
            setSelectedItem(defaultClassLoader);
        }
    }

    public void getAllClassLoaderOneTime(boolean cache) {
        if (!init) {
            getAllClassLoader(cache);
        }
        init = true;
    }

    public void clearOneTimeStatus() {
        init = false;
    }

    public void setSelectedClassLoader(AllClassLoaderRes.Item identity) {
        if (identity == null) {
            return;
        }
        setSelectedItem(identity);
    }

}
