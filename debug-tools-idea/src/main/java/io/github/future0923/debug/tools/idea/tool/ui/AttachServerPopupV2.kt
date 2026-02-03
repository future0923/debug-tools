package io.github.future0923.debug.tools.idea.tool.ui

import com.intellij.icons.AllIcons
import com.intellij.ide.HelpTooltip
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.JBColor
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle
import io.github.future0923.debug.tools.idea.model.ServerDisplayValue
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState
import io.github.future0923.debug.tools.idea.utils.DebugToolsAttachUtils
import io.github.future0923.debug.tools.idea.utils.StateUtils
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class AttachServerPopupV2(private val project: Project) {
    private val listPanel = JPanel(BorderLayout())
    private val rootPanel = panel {
        row {
            cell(listPanel)
                .align(Align.FILL)
                .applyToComponent {
                    preferredSize = Dimension(420, 300)
                    border = JBUI.Borders.empty()
                }
        }.resizableRow()
    }
    private val cardListPanelV2 = CardListPanelV2()
    private val radioButtons: MutableList<JBRadioButton> = ArrayList()
    private var popup: JBPopup? = null
    private var isCollapsed = false

    // --- 修改点：定义固定的滚动条和容器 ---
    private val scrollPane = JBScrollPane(cardListPanelV2.container).apply {
        border = JBUI.Borders.empty()
        verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        viewport.isOpaque = false
        isOpaque = false
    }

    init {
        val actionGroup = DefaultActionGroup().apply {
            add(object : AnAction("Refresh", null, AllIcons.Actions.BuildLoadChanges) {
                override fun actionPerformed(e: AnActionEvent): Unit = loadVmAsync()
            })
            var finalCollapsed = isCollapsed
            add(object : AnAction(if (finalCollapsed) "Expand" else "Collapse", null, AllIcons.Actions.Expandall) {
                override fun update(e: AnActionEvent) {
                    // 动态切换图标
                    e.presentation.icon = if (isCollapsed) AllIcons.Actions.Expandall else AllIcons.Actions.Collapseall
                }

                override fun actionPerformed(e: AnActionEvent) {
                    isCollapsed = !isCollapsed
                    loadVmAsync() // 重新刷新列表以应用新布局
                }
            })
            add(object : AnAction("loading", null, AnimatedIcon.Default()) {
                override fun actionPerformed(e: AnActionEvent): Unit = loadVmAsync()
            })
        }
        val toolbar = ActionManager.getInstance().createActionToolbar("AttachServerToolbar", actionGroup, true)
        toolbar.targetComponent = listPanel
        val headerPanel = JPanel(GridBagLayout()).apply {
            isOpaque = false
            border = JBUI.Borders.empty(5, 10)
            val gbc = GridBagConstraints()
            // 1. 左侧占位：放一个和右侧 Toolbar 等宽的透明组件，保证标题真正居中
            add(Box.createHorizontalStrut(toolbar.component.preferredSize.width), gbc)
            // 2. 中间标题：设置 weightx 让它占据剩余空间，并设置水平居中
            gbc.weightx = 1.0
            gbc.fill = GridBagConstraints.HORIZONTAL
            add(JBLabel(DebugToolsBundle.message("attach.server.title")).apply {
                font = font.deriveFont(Font.BOLD)
                horizontalAlignment = SwingConstants.CENTER
            }, gbc)
            // 3. 右侧按钮：靠右对齐，weightx 设为 0
            gbc.weightx = 0.0
            gbc.fill = GridBagConstraints.NONE
            add(toolbar.component, gbc)
        }
        listPanel.add(headerPanel, BorderLayout.NORTH)
        listPanel.add(scrollPane, BorderLayout.CENTER)
    }

    fun show(location: Point) {
        popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(rootPanel, null)
            .setResizable(true)
            .setMovable(false)
            .setRequestFocus(true)
            .setCancelOnClickOutside(true)
            .setCancelOnOtherWindowOpen(true)
            .createPopup()

        loadVmAsync()
        popup?.show(RelativePoint(location))
    }

    /**
     * 后台异步加载 JVM（绝不阻塞 EDT）
     */
    private fun loadVmAsync() {

        ApplicationManager.getApplication().executeOnPooledThread {
            val jvmList = DebugToolsAttachUtils.vmList()

            ApplicationManager.getApplication().invokeLater({
                if (jvmList.isEmpty()) {
                    updateListContent(
                        JBLabel(
                            DebugToolsBundle.message("attach.server.menu.no.server.found"),
                            SwingConstants.CENTER
                        )
                    )
                } else {
                    cardListPanelV2.container.removeAll()
                    jvmList.forEach { descriptor ->
                        cardListPanelV2.addCard(
                            CardData(
                                descriptor.id ?: "unknown",
                                "Idle",
                                descriptor.displayName ?: "N/A"
                            ), isCollapsed
                        )
                    }
                    updateListContent(cardListPanelV2.container)
                }
            }, project.disposed)
        }
    }

    /**
     * 工具方法：利用 DSL 动态刷新 listPanel 的内容
     */
    private fun updateListContent(content: JComponent) {
        scrollPane.setViewportView(content)
        listPanel.revalidate()
        listPanel.repaint()
    }

    /**
     * 执行 Attach
     */
    private fun doAttach() {
        radioButtons.firstOrNull { it.isSelected }?.let { button ->
            popup?.cancel()
            ApplicationManager.getApplication().executeOnPooledThread {
                val value = ServerDisplayValue.of(button.text) ?: return@executeOnPooledThread

                val settingState = DebugToolsSettingState.getInstance(project)
                val agentPath = settingState.loadAgentPath()

                if (DebugToolsStringUtils.isBlank(agentPath)) {
                    return@executeOnPooledThread
                }

                DebugToolsAttachUtils.attachLocal(
                    project,
                    value.key,
                    value.value,
                    agentPath
                ) {
                    StateUtils.getClassLoaderComboBox(project).refreshClassLoaderLater(true)
                    StateUtils.getPrintSqlPanel(project).refresh()
                    settingState.isLocal = true
                }
            }
        }
    }

    // 1. 数据模型
    data class CardData(val id: String, var status: String, val name: String)

    class CardListPanelV2 {
        // 预先初始化好容器，避免 createPanel 逻辑导致的引用混乱
        val container: JPanel = object : JPanel(FlowLayout(FlowLayout.LEFT, 15, 15)) {
            // 优化：计算高度时考虑折叠状态
            override fun getPreferredSize(): Dimension {
                // 使用固定的宽度参考值，确保计算一致性
                val targetWidth = 390 // 减去滚动条预留宽度
                if (components.isEmpty()) return Dimension(targetWidth, 0)

                var totalHeight = 15
                var currentRowHeight = 0
                var currentRowWidth = 15

                for (comp in components) {
                    val d = comp.preferredSize
                    // 这里的判断逻辑必须和真实的 FlowLayout 换行逻辑完全一致
                    if (currentRowWidth + d.width + 15 > targetWidth) {
                        totalHeight += currentRowHeight + 15
                        currentRowWidth = 15 + d.width
                        currentRowHeight = d.height
                    } else {
                        currentRowWidth += d.width + 15
                        currentRowHeight = maxOf(currentRowHeight, d.height)
                    }
                }
                totalHeight += currentRowHeight + 15 // 最后一行的高度和底部间距
                return Dimension(targetWidth, totalHeight)
            }
        }

        fun clear() {
            container.removeAll()
        }

        fun addCard(data: CardData, isCollapsed: Boolean) {
            container.add(SelectableCard(data, isCollapsed))
        }

        class SelectableCard(private val data: CardData, private val isCollapsed: Boolean) : JPanel(BorderLayout()) {

            private var isSelected = false
            private val arc = 14

            private val content = JPanel().apply {
                isOpaque = false
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                border = JBUI.Borders.empty(5, 10)
            }

            private val nameLabel =
                JBLabel(if (isCollapsed) data.name else data.name.substringAfterLast('.')).apply {
                    font = JBUI.Fonts.label().asBold()
                    horizontalAlignment = SwingConstants.LEFT
                }

            private val idLabel = JBLabel("ID: ${data.id}").apply {
                foreground = JBColor.GRAY
            }

            private val statusLabel = JBLabel(data.status).apply {
                foreground = JBColor.GRAY
            }

            init {
                isOpaque = false
                preferredSize = if (isCollapsed) {
                    Dimension(370, 50)
                } else {
                    Dimension(185, 45)
                }
                nameLabel.alignmentX = LEFT_ALIGNMENT
                // 组装内容
                content.add(nameLabel)
                content.add(Box.createVerticalStrut(4))
                val bottomRow = JPanel(BorderLayout()).apply {
                    isOpaque = false
                    alignmentX = LEFT_ALIGNMENT
                    maximumSize = Dimension(Int.MAX_VALUE, idLabel.preferredSize.height)
                }
                bottomRow.add(idLabel, BorderLayout.WEST)
                bottomRow.add(statusLabel, BorderLayout.EAST)
                content.add(bottomRow)
                add(content)
                updateAppearance()

                addMouseListener(object : MouseAdapter() {
                    override fun mousePressed(e: MouseEvent) {
                        isSelected = !isSelected
                        data.status = if (isSelected) "Active" else "Idle"
                        statusLabel.text = data.status
                        updateAppearance()
                    }
                })
                if (!isCollapsed) {
                    HelpTooltip()
                        .setTitle(data.name)
                        .installOn(this)
                }
            }

            override fun paintComponent(g: Graphics) {
                val g2 = g.create() as Graphics2D
                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                )

                val bg = if (isSelected)
                    JBColor(0xf2fcf3, 0x57965c)
                else
                    JBColor(0xe7effd, 0x25324d)

                g2.color = bg
                g2.fillRoundRect(
                    0, 0,
                    width - 1, height - 1,
                    arc, arc
                )

                g2.dispose()
                super.paintComponent(g)
            }

            override fun paintBorder(g: Graphics) {
                val g2 = g.create() as Graphics2D
                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                )

                g2.color = if (isSelected) JBColor.GREEN else JBColor.border()
                g2.drawRoundRect(
                    0, 0,
                    width - 1, height - 1,
                    arc, arc
                )

                g2.dispose()
            }

            private fun updateAppearance() {
                statusLabel.foreground =
                    if (isSelected) JBColor(0x208a3c, 0x57965c) else JBColor(0xaaaaaa, 0xaaaaaa)
                idLabel.foreground =
                    if (isSelected) JBColor(0x208a3c, 0x57965c) else JBColor(0xaaaaaa, 0xaaaaaa)
                nameLabel.foreground =
                    if (isSelected) JBColor(0x208a3c, 0x57965c) else JBColor(0xaaaaaa, 0xaaaaaa)
                repaint()
            }
        }

    }
}