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

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.idea.search.enums.HttpMethod;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class HttpUrlItem implements NavigationItem {

    @Getter
    private PsiMethod psiMethod;

    @Getter
    private HttpMethod method;

    @Getter
    private String path;

    private final String moduleName;

    private final String className;

    private final String methodName;

    private final String comment;

    private Navigatable navigationElement;

    private Icon icon = DebugToolsIcons.HttpMethod.Request;

    public HttpUrlItem(PsiElement psiElement, HttpMethod method, String urlPath, String moduleName, String className, String methodName, String comment) {
        if (psiElement instanceof PsiMethod) {
            this.psiMethod = (PsiMethod) psiElement;
        }
        if (method != null) {
            this.method = method;
        }
        this.path = urlPath;
        if (psiElement instanceof Navigatable) {
            navigationElement = (Navigatable) psiElement;
        }
        this.comment = comment;
        this.moduleName = moduleName;
        this.className = className;
        this.methodName = methodName;
    }

    @Nullable
    @Override
    public String getName() {
        return this.path;
    }

    @Nullable
    @Override
    @Contract(" -> new")
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {

            @Nullable
            @Override
            public String getPresentableText() {
                return getPath();
            }

            @Override
            public String getLocationString() {
                String location = "";
                String classMethodInfo = "";
                if (StrUtil.isNotBlank(comment)) {
                    location += "[" + StrUtil.subPre(comment, 40) + "]";
                }

                if (StrUtil.isNotBlank(className)) {
                    classMethodInfo = className;
                }

                if (StrUtil.isNotBlank(methodName)) {
                    classMethodInfo += "#" + psiMethod.getName();
                }

                location += "(" + classMethodInfo + ")";

                if (StrUtil.isNotBlank(moduleName)) {
                    location += " in Module '" + moduleName + "'";
                }
                return location;
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return HttpMethod.getIcon(method);
            }
        };
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (navigationElement != null) {
            navigationElement.navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return navigationElement.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    @NotNull
    public HttpUrlItem copyWithParent(@Nullable HttpUrlItem parent) {
        HttpUrlItem requestInfo = new HttpUrlItem(this.psiMethod, this.method, this.path, this.moduleName, this.className, this.methodName, this.comment);
        if (parent != null) {
            requestInfo.setParent(parent);
        }
        return requestInfo;
    }

    public void setParent(@NotNull HttpUrlItem parent) {
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

    public void setMethod(@Nullable HttpMethod method) {
        this.method = method;
        this.icon = HttpMethod.getIcon(method);
    }

    public void setPath(@NotNull String path) {
        path = path.trim();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        path = path.replaceAll("//", "/");
        this.path = path;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof HttpUrlItem that)) return false;

        return method == that.method && Objects.equals(path, that.path) && Objects.equals(moduleName, that.moduleName) && Objects.equals(className, that.className) && Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(method);
        result = 31 * result + Objects.hashCode(path);
        result = 31 * result + Objects.hashCode(moduleName);
        result = 31 * result + Objects.hashCode(className);
        result = 31 * result + Objects.hashCode(methodName);
        return result;
    }
}
