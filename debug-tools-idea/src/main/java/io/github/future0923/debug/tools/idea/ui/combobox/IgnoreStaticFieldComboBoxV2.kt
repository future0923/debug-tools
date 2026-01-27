package io.github.future0923.debug.tools.idea.ui.combobox

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.panel
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState
import io.github.future0923.debug.tools.idea.ui.main.ConfDialogWrapper
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil
import javax.swing.JPanel

class IgnoreStaticFieldComboBoxV2(private val project: Project) {

    lateinit var comboBox: Cell<ComboBox<String>>

    val component: JPanel by lazy {
        panel {
            row {
                comboBox = comboBox(mutableListOf<String>())
                    .applyToComponent {
                        addActionListener {
                            updateButtonsVisibility()
                        }
                    }
                button(DebugToolsBundle.message("action.reload")) {
                    reload()
                    DebugToolsNotifierUtil.notifyInfo(project, DebugToolsBundle.message("reload.successful"))
                }
                button(DebugToolsBundle.message("action.detail")) {
                    val wrapper =
                        ConfDialogWrapper(project, getLegacyWrapper(), comboBox.component.selectedItem as? String)
                    wrapper.show()
                }.applyToComponent { detailButton = this }
                button(DebugToolsBundle.message("action.delete")) {
                    val settingState = DebugToolsSettingState.getInstance(project)
                    val selectedItem = comboBox.component.selectedItem as? String
                    val filePath = settingState.ignoreStaticFieldPathMap.remove(selectedItem)
                    if (StrUtil.isNotBlank(filePath) && FileUtil.exist(filePath)) {
                        FileUtil.del(filePath)
                    }
                    refresh()
                }.applyToComponent { deleteButton = this }
                button(DebugToolsBundle.message("action.add")) {
                    val wrapper = ConfDialogWrapper(project, getLegacyWrapper(), "")
                    wrapper.show()
                }
            }
        }
    }

    private var detailButton: javax.swing.JButton? = null
    private var deleteButton: javax.swing.JButton? = null

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
        val selected = comboBox.component.selectedItem
        comboBox.component.removeAllItems()
        val settingState = DebugToolsSettingState.getInstance(project)
        settingState.ignoreStaticFieldPathMap.keys.forEach {
            comboBox.component.addItem(it)
        }
        if (selected != null) {
            comboBox.component.selectedItem = selected
        }
        updateButtonsVisibility()
    }

    fun setSelected(identity: String?) {
        checkInitialized()
        if (identity == null) {
            comboBox.component.selectedItem = "default"
        } else {
            comboBox.component.selectedItem = identity
        }
        updateButtonsVisibility()
    }

    fun getSelectedItem(): String? {
        checkInitialized()
        return comboBox.component.selectedItem as? String
    }

    private fun updateButtonsVisibility() {
        val selected = StrUtil.isNotBlank(comboBox.component.selectedItem as? String)
        detailButton?.isVisible = selected
        deleteButton?.isVisible = selected
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
}