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
package io.github.future0923.debug.tools.idea.search.beans;

import com.intellij.psi.NavigatablePsiElement;
import io.github.future0923.debug.tools.idea.search.enums.HttpMethod;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * 请求信息
 *
 * @author future0923
 */
public class HttpUrlInfo {

    @Getter
    private final NavigatablePsiElement psiElement;

    @Nullable
    private HttpMethod method;

    @Nullable
    private String path;

    @NotNull
    private Icon icon = DebugToolsIcons.HttpMethod.Request;

    public HttpUrlInfo(HttpMethod method, @Nullable String path, @Nullable NavigatablePsiElement psiElement) {
        this.setMethod(method);
        if (path != null) {
            this.setPath(path);
        }
        this.psiElement = psiElement;
    }

    public void navigate(boolean requestFocus) {
        if (psiElement != null) {
            psiElement.navigate(requestFocus);
        }
    }

    @Nullable
    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(@Nullable HttpMethod method) {
        this.method = method;
        this.icon = HttpMethod.getIcon(method);
    }

    @NotNull
    public Icon getIcon() {
        return icon;
    }

    @Nullable
    public String getPath() {
        return path;
    }

    public void setPath(@NotNull String path) {
        path = path.trim();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        path = path.replaceAll("//", "/");
        this.path = path;
    }

    public void setParent(@NotNull HttpUrlInfo parent) {
        if ((this.method == null || this.method == HttpMethod.REQUEST) && parent.getMethod() != null) {
            this.setMethod(parent.getMethod());
        }
        String parentPath = parent.getPath();
        if (parentPath != null && parentPath.endsWith("/")) {
            // 去掉末尾的斜杠
            parentPath = parentPath.substring(0, parentPath.length() - 1);
        }
        this.setPath(parentPath + this.path);
    }

    @NotNull
    public HttpUrlInfo copyWithParent(@Nullable HttpUrlInfo parent) {
        HttpUrlInfo requestInfo = new HttpUrlInfo(this.method, this.path, this.psiElement);
        if (parent != null) {
            requestInfo.setParent(parent);
        }
        return requestInfo;
    }

    @NotNull
    public String getIdentity(String... itemIds) {
        HttpMethod method = this.method == null ? HttpMethod.REQUEST : this.method;
        String path = this.path == null ? "" : this.path;

        StringBuilder items = new StringBuilder();
        if (itemIds != null) {
            items.append("-[");
            for (int i = 0; i < itemIds.length; i++) {
                if (i > 0) {
                    items.append(", ");
                }
                items.append(itemIds[i]);
            }
            items.append("]");
        }

        return String.format("{}[%s]%s(%s)%s", method, path, icon.getClass(), items);
    }

    @Override
    public String toString() {
        return this.getIdentity();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpUrlInfo httpUrlInfo = (HttpUrlInfo) o;
        if (method != httpUrlInfo.method) {
            return false;
        }
        return Objects.equals(path, httpUrlInfo.path);
    }

    @Override
    public int hashCode() {
        int result = method != null ? method.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}
