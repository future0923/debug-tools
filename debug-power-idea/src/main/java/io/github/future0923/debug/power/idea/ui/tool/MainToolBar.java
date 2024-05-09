package io.github.future0923.debug.power.idea.ui.tool;

import com.intellij.ui.components.panels.HorizontalBox;
import io.github.future0923.debug.power.idea.utils.DebugPowerIconUtil;

import javax.swing.*;

/**
 * @author future0923
 */
public class MainToolBar extends ToolBar{

    public MainToolBar() {
        super();
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, this.getBackground().darker()));
        initButtons();
        super.add(new HorizontalBox());
    }

    private void initButtons() {
        genButton("Gen Param", DebugPowerIconUtil.example_icon, DebugPowerIconUtil.example_icon, actionEvent -> {

        });
    }
}
