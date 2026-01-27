package io.github.future0923.debug.tools.idea.ui.combobox

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.actionButton
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.listCellRenderer.listCellRenderer
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState
import io.github.future0923.debug.tools.idea.ui.main.ConfDialogWrapper
import javax.swing.JPanel

class IgnoreStaticFieldComboBoxV2(private val project: Project) {

    lateinit var comboBox: Cell<ComboBox<Any>>
    private var lastValidSelection: String? = null
    private var isRefreshing = false

    val component: JPanel by lazy {
        panel {
            row {
                @Suppress("UnstableApiUsage")
                comboBox = comboBox(mutableListOf<Any>(), listCellRenderer {
                    when (val item = value) {
                        is ComboAction -> {
                            // 如果是第一个 Action (ADD)，在其上方画一条带文字的分隔线
                            if (item == ComboAction.ADD) {
                                separator { text = "Operations" }
                            }
                            text(item.getText())
                        }

                        is String -> {
                            text(item)
                        }
                    }
                }).columns(19).applyToComponent {
                    addActionListener {
                        val selected = selectedItem
                        handleSelection(selected)
                    }
                }
                // 创建一个 ActionGroup
                val group = object : DefaultActionGroup("Settings", true) {
                    init {
                        // 使用“更多”图标
                        templatePresentation.icon = AllIcons.General.GearPlain
                        // 添加具体项
                        add(object : AnAction(DebugToolsBundle.message("action.reload"), null, null) {
                            override fun actionPerformed(e: AnActionEvent) = reload()
                        })
                        addSeparator()
                        ComboAction.entries.forEach { action ->
                            add(object : AnAction(action.getText(), null, null) {
                                override fun actionPerformed(e: AnActionEvent) = handleSelection(action)
                            })
                        }
                    }

                    override fun actionPerformed(e: AnActionEvent) {
                        val popup = JBPopupFactory.getInstance().createActionGroupPopup(
                            null, this, e.dataContext, JBPopupFactory.ActionSelectionAid.MNEMONICS, true
                        )
                        e.inputEvent?.let { popup.showUnderneathOf(it.component) }
                    }
                }
                actionButton(group)
            }
        }
    }

    private fun handleSelection(selected: Any?) {
        if (isRefreshing) return

        when (selected) {
            ComboAction.ADD -> {
                val wrapper = ConfDialogWrapper(project, getLegacyWrapper(), "")
                wrapper.show()
                refresh() // 刷新列表，并在 refresh 里把选中项拨回正常数据项
            }

            ComboAction.DETAIL -> {
                lastValidSelection?.let {
                    ConfDialogWrapper(project, getLegacyWrapper(), it).show()
                }
                refresh()
            }

            ComboAction.DELETE -> {
                lastValidSelection?.let { key ->
                    val path = DebugToolsSettingState.getInstance(project).ignoreStaticFieldPathMap.remove(key)
                    if (StrUtil.isNotBlank(path)) FileUtil.del(path)
                }
                lastValidSelection = null
                refresh()
            }

            is String -> {
                lastValidSelection = selected
            }
        }
    }

    init {
        val defaultConfPath = project.basePath + IdeaPluginProjectConstants.IGNORE_STATIC_FIELD_DIR + "default.conf"
        if (!FileUtil.exist(defaultConfPath)) {
            FileUtil.touch(defaultConfPath)
        }
        reload()
    }

    private fun checkInitialized() {
        if (!this::comboBox.isInitialized) {
            component
        }
    }

    fun reload() {
        checkInitialized()
        val settingState = DebugToolsSettingState.getInstance(project)
        val defaultConfPath = project.basePath + IdeaPluginProjectConstants.IGNORE_STATIC_FIELD_DIR + "default.conf"
        if (!FileUtil.exist(defaultConfPath)) {
            FileUtil.touch(defaultConfPath)
        }
        val dir = project.basePath + IdeaPluginProjectConstants.IGNORE_STATIC_FIELD_DIR
        val files = FileUtil.loopFiles(dir)
        settingState.ignoreStaticFieldPathMap.clear()
        for (file in files) {
            settingState.ignoreStaticFieldPathMap[FileUtil.mainName(file)] = file.absolutePath
        }
        refresh()
    }

    fun refresh() {
        checkInitialized()
        isRefreshing = true
        val model = comboBox.component.model as javax.swing.DefaultComboBoxModel<Any>
        model.removeAllElements()

        // 1. 放入数据项
        val settingState = DebugToolsSettingState.getInstance(project)
        val keys = settingState.ignoreStaticFieldPathMap.keys.sorted()
        keys.forEach { model.addElement(it) }

        // 2. 放入操作项
        model.addElement(ComboAction.ADD)

        // 3. 恢复选中逻辑优化
        if (lastValidSelection != null && keys.contains(lastValidSelection)) {
            // 如果上次选中的还在，继续选中它
            comboBox.component.selectedItem = lastValidSelection
        } else if (keys.isNotEmpty()) {
            // 如果上次选中的没了，但还有其他配置，选中第一个
            comboBox.component.selectedIndex = 0
            lastValidSelection = keys[0]
        } else {
            // --- 关键点：如果没有正常配置项了 ---
            comboBox.component.selectedItem = null
            lastValidSelection = null
        }

        isRefreshing = false
    }

    fun setSelected(identity: String?) {
        checkInitialized()
        if (identity == null) {
            comboBox.component.selectedItem = "default"
        } else {
            comboBox.component.selectedItem = identity
        }
    }

    fun getSelectedItem(): String? {
        checkInitialized()
        return comboBox.component.selectedItem as? String
    }

    private fun getLegacyWrapper(): IgnoreStaticFieldComboBox {
        return object : IgnoreStaticFieldComboBox(project) {
            override fun refresh() {
                this@IgnoreStaticFieldComboBoxV2.refresh()
            }

            override fun setSelected(identity: String?) {
                this@IgnoreStaticFieldComboBoxV2.setSelected(identity)
            }

            override fun getSelectedItem(): Any? {
                return this@IgnoreStaticFieldComboBoxV2.getSelectedItem()
            }
        }
    }

    private enum class ComboAction(val messageKey: String) {
        ADD("action.add"),
        DETAIL("action.detail"),
        DELETE("action.delete");

        fun getText() = DebugToolsBundle.message(messageKey)
    }
}