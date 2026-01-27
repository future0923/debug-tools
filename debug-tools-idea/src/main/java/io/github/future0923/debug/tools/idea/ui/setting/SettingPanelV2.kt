package io.github.future0923.debug.tools.idea.ui.setting


import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.listCellRenderer.textListCellRenderer
import io.github.future0923.debug.tools.base.enums.PrintSqlType
import io.github.future0923.debug.tools.base.logging.Logger
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle
import io.github.future0923.debug.tools.idea.setting.GenParamType
import io.github.future0923.debug.tools.idea.setting.LanguageSetting
import io.github.future0923.debug.tools.idea.ui.combobox.IgnoreStaticFieldComboBox
import io.github.future0923.debug.tools.idea.ui.main.IgnoreSqlConfDialogWrapper
import io.github.future0923.debug.tools.idea.ui.main.TraceMethodPanel
import javax.swing.JPanel

private lateinit var saveSqlCheck: Cell<JBCheckBox>
fun settingPanel(project: Project): JPanel {
    val traceMethodPanel = TraceMethodPanel()
    val ignoreStaticFieldComboBox = IgnoreStaticFieldComboBox(project, 220)
    return panel {
        group("Basic") {
            row(DebugToolsBundle.message("setting.panel.language")) {
                comboBox(
                    listOf(
                        LanguageSetting.IDE,
                        LanguageSetting.ENGLISH,
                        LanguageSetting.CHINESE
                    ),
                    textListCellRenderer { it?.displayName }
                )
            }
            row(DebugToolsBundle.message("setting.panel.log.level")) {
                comboBox(
                    Logger.Level.entries,
                    textListCellRenderer { it?.name?.lowercase()?.replaceFirstChar { c -> c.uppercase() } }
                )
            }
            row(DebugToolsBundle.message("setting.panel.entity.class.default.param")) {
                comboBox(
                    GenParamType.entries,
                    textListCellRenderer { DebugToolsBundle.message(it?.bundleKey ?: "") }
                )
            }
        }

        group("Quick Debug") {
            row {
                checkBox(DebugToolsBundle.message("setting.panel.invoke.method.record"))
            }
            row {
                checkBox(DebugToolsBundle.message("setting.panel.quick.action.line.marker"))
            }
            row {
                checkBox(DebugToolsBundle.message("setting.panel.search.library"))
            }
            row {
                checkBox(DebugToolsBundle.message("setting.panel.auto.attach.start.application"))
            }
        }

        group("SQL") {
            row(DebugToolsBundle.message("setting.panel.print.sql")) {
                comboBox(
                    listOf(PrintSqlType.PRETTY, PrintSqlType.COMPRESS, PrintSqlType.NO),
                    textListCellRenderer { DebugToolsBundle.message(it?.bundleKey ?: "") }
                )
            }
            row(DebugToolsBundle.message("action.ignore.sql.config")) {
                button("编辑") {
                    IgnoreSqlConfDialogWrapper(project).show()
                }
            }
            row("") {
                saveSqlCheck = checkBox(DebugToolsBundle.message("setting.panel.auto.save.sql"))
            }
            rowsRange {
                row("") {
                    spinner(1..Int.MAX_VALUE)
                    comment(
                        DebugToolsBundle.message("setting.panel.sql.retention.days") + "," + DebugToolsBundle.message(
                            "setting.panel.minimum.settable.value"
                        )
                    )
                }
            }.enabledIf(saveSqlCheck.selected)
        }

        group("Other") {
            row(DebugToolsBundle.message("setting.panel.remove.context.path")) {
                textArea()
            }
            row {
                label(DebugToolsBundle.message("setting.panel.trace.method")).align(AlignY.TOP)
                cell(traceMethodPanel.component)
            }.layout(RowLayout.PARENT_GRID)
            traceMethodPanel.processDefaultInfo(project)
            row(DebugToolsBundle.message("setting.panel.ignore.static.field")) {
                cell(ignoreStaticFieldComboBox.panel)
            }
        }
    }
}