package io.github.future0923.debug.tools.idea.ui.main

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.listCellRenderer.textListCellRenderer
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle
import io.github.future0923.debug.tools.idea.setting.LanguageSetting
import io.github.future0923.debug.tools.idea.ui.combobox.IgnoreStaticFieldComboBoxV2
import javax.swing.JPanel

/**
 * 方法设置面板
 */
class MethodSettingPanel {

    fun createPanel(project: Project): JPanel {
        return panel {
            row("附着进程: ") {
                comboBox(
                    listOf(
                        LanguageSetting.IDE,
                        LanguageSetting.ENGLISH,
                        LanguageSetting.CHINESE
                    ),
                    textListCellRenderer { it?.displayName }
                )
            }
            row(DebugToolsBundle.message("method.around")) {
                IgnoreStaticFieldComboBoxV2(project)
            }
        }
    }
}