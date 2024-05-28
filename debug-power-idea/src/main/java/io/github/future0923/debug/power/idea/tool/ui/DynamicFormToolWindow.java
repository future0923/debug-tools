package io.github.future0923.debug.power.idea.tool.ui;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author future0923
 */
public class DynamicFormToolWindow {
    private JPanel mainPanel;
    private JPanel formPanel;

    public DynamicFormToolWindow(Project project) {
        // 初始化 mainPanel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 初始化表单面板并设置布局
        formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 2, 5, 5));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField();

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(ageLabel);
        formPanel.add(ageField);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField new_nameField = new JTextField();
                JTextField new_ageField = new JTextField();
                formPanel.add(new JLabel("Name:"));
                formPanel.add(new_nameField);
                formPanel.add(new JLabel("Age:"));
                formPanel.add(new_ageField);
                formPanel.revalidate();
                formPanel.repaint();
            }
        });

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component[] components = formPanel.getComponents();
                if (components.length > 4) {
                    for (int i = components.length - 1; i >= components.length - 4; i--) {
                        formPanel.remove(components[i]);
                    }
                    formPanel.revalidate();
                    formPanel.repaint();
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Cannot remove. At least one entry should remain.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public JPanel getContentPanel() {
        return mainPanel;
    }
}