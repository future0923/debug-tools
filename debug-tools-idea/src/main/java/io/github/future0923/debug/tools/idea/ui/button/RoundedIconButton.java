package io.github.future0923.debug.tools.idea.ui.button;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;

import com.intellij.ui.JBColor;

/**
 * 圆角图标按钮
 *
 * @author fathuhu
 */
public class RoundedIconButton extends JButton {

    private Color hoverColor;
    private int arcWidth = 6;
    private int arcHeight = 6;

    // 鼠标是否悬停的内部状态
    private boolean isHovered = false;

    public RoundedIconButton(Icon icon) {
        this(icon, new JBColor(new Color(200, 200, 200, 100), new Color(200, 200, 200, 100)), new Dimension(20, 20));
    }

    public RoundedIconButton(Icon icon, Color hoverColor, Dimension preferredSize) {
        super(icon);
        this.hoverColor = hoverColor;

        // 禁用默认的 Swing 绘制，以便我们自定义绘制
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        this.setPreferredSize(preferredSize);

        // 添加 MouseListener 来控制悬停状态
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                isHovered = false;
                repaint();
            }
        });
    }

    // 覆盖这个方法来绘制自定义的背景
    @Override
    protected void paintComponent(Graphics g) {
        if (isHovered && isEnabled()) {
            Graphics2D g2 = (Graphics2D)g.create();

            // 开启抗锯齿，让圆角更平滑
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 设置悬停颜色
            g2.setColor(hoverColor);

            // 绘制圆角矩形
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arcWidth, arcHeight));

            g2.dispose();
        }

        // 最后调用父类的 paintComponent 来绘制 Icon 和 Text (如果有的话)
        super.paintComponent(g);
    }
}