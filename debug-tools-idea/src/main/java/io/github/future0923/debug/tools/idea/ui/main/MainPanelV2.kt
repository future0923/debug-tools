package io.github.future0923.debug.tools.idea.ui.main

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.impl.JBEditorTabs
import javax.swing.JPanel

@Suppress("UnstableApiUsage")
class MainPanelV2(project: Project, parentDisposable: Disposable) {

    private val tabs: JBEditorTabs = JBEditorTabs(project, parentDisposable)
    private var tabCount = 0

    init {
        tabs.apply {
            // 设置为单行显示，这是实现“自动缩小和收纳”的关键
            setSingleRow(true)
        }

    }

    fun createPanel(): JPanel {
        addNewTab("Main Tab")

        return panel {
            row {
                cell(tabs)
                    .align(Align.FILL)
                    .resizableColumn()

                button("") {
                    addNewTab("New Tab")
                }.applyToComponent {
                    icon = AllIcons.General.Add
                    isContentAreaFilled = false
                    isBorderPainted = false
                }
            }.resizableRow()
        }
    }

    private fun addNewTab(title: String) {
        val labelText = if (tabCount == 0) title else "$title $tabCount"

        // 每个 Tab 的内容容器
        val content = JPanel()

        val info = TabInfo(JPanel()).apply {
            setText(labelText)
            setIcon(AllIcons.Nodes.MethodReference)
        }
        val closeAction = object : AnAction("Close", "Close tab", AllIcons.Actions.Close) {
            override fun actionPerformed(e: AnActionEvent) {
                tabs.removeTab(info)
            }

            // 核心：告诉 IDEA 在 UI 线程更新此 Action
            // 如果不重写，ActionToolbar 可能无法正确获取 Action 的 Presentation 导致图标不显示
            override fun getActionUpdateThread(): ActionUpdateThread {
                return ActionUpdateThread.EDT
            }

            // 强制在工具栏中可见
            override fun update(e: AnActionEvent) {
                e.presentation.isEnabledAndVisible = true
            }
        }
        val group = DefaultActionGroup(closeAction)
        info.setTabLabelActions(group, TabInfo.TAB_ACTION_GROUP)
        tabs.addTab(info)
        tabs.revalidateAndRepaint()
        tabs.select(info, true)
        tabCount++
    }
}