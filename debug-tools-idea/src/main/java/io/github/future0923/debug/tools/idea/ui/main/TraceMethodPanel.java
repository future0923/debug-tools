/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.idea.ui.main;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.common.dto.TraceMethodDTO;

import javax.swing.*;

/**
 * @author future0923
 */
public class TraceMethodPanel {

    private final TraceMethodPanelV2 delegate = new TraceMethodPanelV2();

    public TraceMethodPanel() {
    }

    public void processDefaultInfo(Project project) {
        delegate.processDefaultInfo(project);
    }

    public void processDefaultInfo(Project project, TraceMethodDTO traceMethodDTO) {
        delegate.processDefaultInfo(project, traceMethodDTO);
    }

    public JPanel getComponent() {
        return delegate.getComponent();
    }

    public boolean isTraceMethod() {
        return delegate.isTraceMethod();
    }

    public void setTraceMethod(boolean traceMethod) {
        delegate.setTraceMethod(traceMethod);
    }

    public int getMaxDepth() {
        return delegate.getMaxDepth();
    }

    public void setMaxDepth(int maxDepth) {
        delegate.setMaxDepth(maxDepth);
    }

    public boolean isTraceMyBatis() {
        return delegate.isTraceMyBatis();
    }

    public void setTraceMyBatis(boolean traceMyBatis) {
        delegate.setTraceMyBatis(traceMyBatis);
    }

    public boolean isTraceSql() {
        return delegate.isTraceSql();
    }

    public void setTraceSql(boolean traceSql) {
        delegate.setTraceSql(traceSql);
    }

    public boolean isTraceSkipStartGetSetCheckBox() {
        return delegate.isTraceSkipStartGetSetCheckBox();
    }

    public void setTraceSkipStartGetSetCheckBox(boolean traceSkipStartGetSetCheckBox) {
        delegate.setTraceSkipStartGetSetCheckBox(traceSkipStartGetSetCheckBox);
    }

    public String getTraceBusinessPackage() {
        return delegate.getTraceBusinessPackage();
    }

    public void setTraceBusinessPackage(String traceBusinessPackage) {
        delegate.setTraceBusinessPackage(traceBusinessPackage);
    }

    public String getTraceIgnorePackage() {
        return delegate.getTraceIgnorePackage();
    }

    public void setTraceIgnorePackage(String traceIgnorePackage) {
        delegate.setTraceIgnorePackage(traceIgnorePackage);
    }

}
