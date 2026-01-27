package io.github.future0923.debug.tools.idea.ui.main

import com.intellij.openapi.project.Project
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import io.github.future0923.debug.tools.common.dto.TraceMethodDTO
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState
import javax.swing.JPanel

class TraceMethodPanelV2 {

    private lateinit var traceMethodCheckBox: Cell<JBCheckBox>
    private lateinit var maxDepth: Cell<JBIntSpinner>
    private lateinit var traceMyBatisCheckBox: Cell<JBCheckBox>
    private lateinit var traceSqlCheckBox: Cell<JBCheckBox>
    private lateinit var traceSkipStartGetSetCheckBox: Cell<JBCheckBox>
    private lateinit var traceBusinessPackage: Cell<JBTextField>
    private lateinit var traceIgnorePackage: Cell<JBTextField>

    val component: JPanel by lazy {
        panel {
            row {
                traceMethodCheckBox = checkBox(DebugToolsBundle.message("trace.method.panel.trace.method"))
            }
            rowsRange {
                row {
                    label(DebugToolsBundle.message("trace.method.panel.max.depth"))
                    maxDepth = spinner(1..Int.MAX_VALUE, 1)
                }
                row {
                    traceMyBatisCheckBox = checkBox(DebugToolsBundle.message("trace.method.panel.mybatis"))
                    traceSqlCheckBox = checkBox(DebugToolsBundle.message("trace.method.panel.sql"))
                    traceSkipStartGetSetCheckBox =
                        checkBox(DebugToolsBundle.message("trace.method.panel.skip.get.set.method"))
                }
                row {
                    label(DebugToolsBundle.message("trace.method.panel.business.package"))
                    traceBusinessPackage = textField().columns(COLUMNS_MEDIUM)
                }
                row {
                    label(DebugToolsBundle.message("trace.method.panel.ignore.package"))
                    traceIgnorePackage = textField().columns(COLUMNS_MEDIUM)
                }
            }.enabledIf(traceMethodCheckBox.selected)
        }
    }

    private fun checkInitialized() {
        if (!this::traceMethodCheckBox.isInitialized) {
            component
        }
    }

    fun processDefaultInfo(project: Project) {
        val settingState = DebugToolsSettingState.getInstance(project)
        var traceMethodDTO = settingState.traceMethodDTO
        if (traceMethodDTO == null) {
            traceMethodDTO = TraceMethodDTO()
        }
        processDefaultInfo(project, traceMethodDTO)
    }

    fun processDefaultInfo(project: Project, traceMethodDTO: TraceMethodDTO?) {
        checkInitialized()
        if (traceMethodDTO != null) {
            traceMethodDTO.traceMethod?.let { traceMethodCheckBox.component.isSelected = it }
            traceMethodDTO.traceMaxDepth?.let { maxDepth.component.value = it }
            traceMethodDTO.traceMyBatis?.let { traceMyBatisCheckBox.component.isSelected = it }
            traceMethodDTO.traceSQL?.let { traceSqlCheckBox.component.isSelected = it }
            traceMethodDTO.traceSkipStartGetSetCheckBox?.let { traceSkipStartGetSetCheckBox.component.isSelected = it }
            traceMethodDTO.traceBusinessPackageRegexp?.let { traceBusinessPackage.component.text = it }
            traceMethodDTO.traceIgnorePackageRegexp?.let { traceIgnorePackage.component.text = it }
        } else {
            processDefaultInfo(project)
        }
    }

    fun isTraceMethod(): Boolean {
        checkInitialized()
        return traceMethodCheckBox.component.isSelected
    }

    fun setTraceMethod(traceMethod: Boolean) {
        checkInitialized()
        traceMethodCheckBox.component.isSelected = traceMethod
    }

    fun getMaxDepth(): Int {
        checkInitialized()
        return maxDepth.component.value as Int
    }

    fun setMaxDepth(maxDepth: Int) {
        checkInitialized()
        this.maxDepth.component.value = maxDepth
    }

    fun isTraceMyBatis(): Boolean {
        checkInitialized()
        return traceMyBatisCheckBox.component.isSelected
    }

    fun setTraceMyBatis(traceMyBatis: Boolean) {
        checkInitialized()
        traceMyBatisCheckBox.component.isSelected = traceMyBatis
    }

    fun isTraceSql(): Boolean {
        checkInitialized()
        return traceSqlCheckBox.component.isSelected
    }

    fun setTraceSql(traceSql: Boolean) {
        checkInitialized()
        traceSqlCheckBox.component.isSelected = traceSql
    }

    fun isTraceSkipStartGetSetCheckBox(): Boolean {
        checkInitialized()
        return traceSkipStartGetSetCheckBox.component.isSelected
    }

    fun setTraceSkipStartGetSetCheckBox(traceSkipStartGetSetCheckBox: Boolean) {
        checkInitialized()
        this.traceSkipStartGetSetCheckBox.component.isSelected = traceSkipStartGetSetCheckBox
    }

    fun getTraceBusinessPackage(): String {
        checkInitialized()
        return traceBusinessPackage.component.text
    }

    fun setTraceBusinessPackage(traceBusinessPackage: String) {
        checkInitialized()
        this.traceBusinessPackage.component.text = traceBusinessPackage
    }

    fun getTraceIgnorePackage(): String {
        checkInitialized()
        return traceIgnorePackage.component.text
    }

    fun setTraceIgnorePackage(traceIgnorePackage: String) {
        checkInitialized()
        this.traceIgnorePackage.component.text = traceIgnorePackage
    }
}