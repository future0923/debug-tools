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
package io.github.future0923.debug.tools.idea.model;

import java.util.Objects;

/**
 * 参数数据信息
 *
 * @author future0923
 */
public class ParamDataItem {

    protected String name;

    protected String qualifiedName;

    protected String param;

    public ParamDataItem() {
    }

    public ParamDataItem(String name, String qualifiedName, String param) {
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.param = param;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }


    public String methodName() {
        return Objects.isNull(qualifiedName) ? null : qualifiedName.substring(qualifiedName.lastIndexOf("#") + 1);
    }
}