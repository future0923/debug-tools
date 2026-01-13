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
package io.github.future0923.debug.tools.idea.search;

import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.FoundItemDescriptor;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.ide.util.gotoByName.GotoFileCellRenderer;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFilePathWrapper;
import com.intellij.openapi.vfs.newvfs.VfsPresentationUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.Processor;
import com.intellij.util.ui.UIUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.idea.search.beans.HttpUrlItem;
import io.github.future0923.debug.tools.idea.search.beans.SearchWhereHttpUrlItem;
import io.github.future0923.debug.tools.idea.search.utils.HttpUrlUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 集成到 Search Anywhere (Double Shift) - 老版本 SDK 适配
 * @author caoayu
 */
public class HttpUrlSearchEverywhereContributor extends AbstractGotoSEContributor {
    private AnActionEvent event;
    private Project project;

    public HttpUrlSearchEverywhereContributor(@NotNull AnActionEvent event) {
        super(event);
        this.event = event;
        this.project = event.getProject();
    }

    @Override
    public @Nls String getAdvertisement() {
        return super.getAdvertisement();
    }

    @Override
    public boolean isEmptyPatternSupported() {
        return false;
    }

    @Override
    public void fetchWeightedElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super FoundItemDescriptor<Object>> consumer) {
        if (isDumbAware()) {
            return;
        }
        // 如果没有输入内容，直接返回，避免加载所有 URL
        if (StringUtil.isEmptyOrSpaces(pattern)) {
            return;
        }
        progressIndicator.start();
        Map<String, HttpUrlItem[]> allController = ApplicationManager.getApplication().runReadAction((Computable<Map<String, HttpUrlItem[]>>) () -> {
            // 双重检查，因为在等待 ReadLock 的过程中项目可能被关闭
            if (project.isDisposed()) {
                return Collections.emptyMap();
            }
            return HttpUrlUtils.getAllRequest(project);
        });

        String searchPattern = removeParam(pattern);

        // 核心修改：
        // 1. 使用 NameUtil.MatchingCaseSensitivity.NONE 忽略大小写
        // 2. 模式字符串前加 "*" 确保可以匹配路径中间的字符串 (Contains 逻辑)
        // 3. 移除末尾的 "*" (通常不需要，NameUtil 会自动处理剩余部分的匹配，加上反而可能导致某些精确匹配逻辑混淆，保留也可以但没必要)
        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + searchPattern, NameUtil.MatchingCaseSensitivity.NONE);

        List<HttpUrlItem> mathchController = new ArrayList<>();
        allController.forEach((name, items) -> {
            for (HttpUrlItem item : items) {
                // 使用 matcher 进行匹配
                if (matcher.matches(item.getPath()) || (StrUtil.isNotBlank(item.getComment()) && matcher.matches(item.getComment()))) {
                    mathchController.add(item);
                }
            }
        });

        List<SearchWhereHttpUrlItem> searchWhereHttpUrlItems = mathchController.stream().sorted(Comparator.comparingInt(o -> o.getPath().length())).map(controller ->
                new SearchWhereHttpUrlItem(controller.getPsiMethod(), controller.getMethod(), controller.getPath(),
                        controller.getModuleName(), controller.getClassName(), controller.getMethodName(), controller.getComment())
        ).toList();
        for (SearchWhereHttpUrlItem controller : searchWhereHttpUrlItems) {
            if (!consumer.process(new FoundItemDescriptor<>(controller, 0))) {
                return;
            }
        }

    }

    @Override
    public @NotNull ListCellRenderer<Object> getElementsRenderer() {
        return new PsiElementListCellRenderer<>() {
            @Override
            public String getElementText(PsiElement element) {
                VirtualFile file = element instanceof PsiFile ? PsiUtilCore.getVirtualFile(element) : element instanceof VirtualFile ? (VirtualFile) element : null;
                if (file != null) {
                    return VfsPresentationUtil.getPresentableNameForUI(element.getProject(), file);
                }

                if (element instanceof NavigationItem) {
                    String name = Optional.ofNullable(((NavigationItem) element).getPresentation()).map(presentation -> presentation.getPresentableText()).orElse(null);
                    if (name != null) return name;
                }

                String name = element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : null;
                return StringUtil.notNullize(name, "<unnamed>");
            }

            @Override
            protected int getIconFlags() {
                return 0;
            }

            @Override
            protected @Nullable String getContainerText(PsiElement element, String name) {
                return getContainerTextForLeftComponent(element, name, -1, null);
            }

            @Nullable
            @Override
            protected String getContainerTextForLeftComponent(PsiElement element, String name, int maxWidth, FontMetrics fm) {
                String presentablePath = extractPresentablePath(element);
                String text = ObjectUtils.chooseNotNull(presentablePath, SymbolPresentationUtil.getSymbolContainerText(element));

                if (text == null || text.equals(name)) return null;

                if (text.startsWith("(") && text.endsWith(")")) {
                    text = text.substring(1, text.length() - 1);
                }

                if (presentablePath == null && (text.contains("/") || text.contains(File.separator)) && element instanceof PsiFileSystemItem) {
                    Project project = element.getProject();
                    String basePath = Optional.ofNullable(project.getBasePath()).map(FileUtil::toSystemDependentName).orElse(null);
                    VirtualFile file = ((PsiFileSystemItem) element).getVirtualFile();
                    if (file != null) {
                        text = FileUtil.toSystemDependentName(text);
                        String filePath = FileUtil.toSystemDependentName(file.getPath());
                        if (basePath != null && FileUtil.isAncestor(basePath, filePath, true)) {
                            text = ObjectUtils.notNull(FileUtil.getRelativePath(basePath, text, File.separatorChar), text);
                        } else {
                            String rootPath = Optional.ofNullable(GotoFileCellRenderer.getAnyRoot(file, project)).map(root -> FileUtil.toSystemDependentName(root.getPath())).filter(root -> basePath != null && FileUtil.isAncestor(basePath, root, true)).orElse(null);
                            text = rootPath != null ? ObjectUtils.notNull(FileUtil.getRelativePath(rootPath, text, File.separatorChar), text) : FileUtil.getLocationRelativeToUserHome(text);
                        }
                    }
                }

                boolean in = text.startsWith("in ");
                if (in) text = text.substring(3);
                String left = in ? "in " : "";
                String adjustedText = left + text;
                if (maxWidth < 0) return adjustedText;

                int fullWidth = fm.stringWidth(adjustedText);
                if (fullWidth < maxWidth) return adjustedText;
                String separator = text.contains("/") ? "/" : SystemInfo.isWindows && text.contains("\\") ? "\\" : text.contains(".") ? "." : text.contains("-") ? "-" : " ";
                LinkedList<String> parts = new LinkedList<>(StringUtil.split(text, separator));
                int index;
                while (parts.size() > 1) {
                    index = parts.size() / 2 - 1;
                    parts.remove(index);
                    if (fm.stringWidth(left + StringUtil.join(parts, separator) + "...") < maxWidth) {
                        parts.add(index, "...");
                        return left + StringUtil.join(parts, separator);
                    }
                }
                int adjustedWidth = Math.max(adjustedText.length() * maxWidth / fullWidth - 1, left.length() + 3);
                return StringUtil.trimMiddle(adjustedText, adjustedWidth);
            }

            @Nullable
            private String extractPresentablePath(@Nullable PsiElement element) {
                if (element == null) return null;

                PsiFile file = element.getContainingFile();
                if (file != null) {
                    VirtualFile virtualFile = file.getVirtualFile();
                    if (virtualFile instanceof VirtualFilePathWrapper)
                        return ((VirtualFilePathWrapper) virtualFile).getPresentablePath();
                }

                return null;
            }


            @Override
            protected boolean customizeNonPsiElementLeftRenderer(ColoredListCellRenderer renderer, JList list, Object value, int index, boolean selected, boolean hasFocus) {
                if (!(value instanceof NavigationItem)) return false;
                NavigationItem item = (NavigationItem) value;
                Color fgColor = list.getForeground();
                Color bgColor = UIUtil.getListBackground();
                SimpleTextAttributes urlNameAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, fgColor);
                ItemMatchers itemMatchers = getItemMatchers(list, value);

                ItemPresentation presentation = Objects.requireNonNull(item.getPresentation());
                String presentableText = presentation.getPresentableText();
                if (StrUtil.isEmpty(presentableText)) {
                    return false;
                }

                presentableText = presentableText.replaceFirst("in\\ Module\\[\\d+]:\\s*", "");
                SpeedSearchUtil.appendColoredFragmentForMatcher(presentableText, renderer, urlNameAttributes, itemMatchers.nameMatcher, bgColor, selected);
                renderer.setIcon(presentation.getIcon(true));
                String locationString = presentation.getLocationString();
                if (!StringUtil.isEmpty(locationString)) {
                    renderer.append(" " + locationString, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GRAY));
                }

                return true;
            }

            @Override
            protected @Nullable DefaultListCellRenderer getRightCellRenderer(Object value) {
                DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof HttpUrlItem) {
                            ((DefaultListCellRenderer) component).setText(((HttpUrlItem) value).getName());
                        }
                        return component;
                    }
                };
                return defaultListCellRenderer;
            }
        };
    }

    private static String removeParam(String url) {
        try {
            URL newUrl = new URL(url);
            return newUrl.getPath();
        } catch (MalformedURLException e) {
        }
        if (url.contains("?")) url = url.substring(0, url.indexOf('?'));
        return url;
    }

    @Override
    protected @NotNull FilteringGotoByModel<?> createModel(@NotNull Project project) {
        return null;
    }

    @Override
    public boolean processSelectedItem(@NotNull Object selected, int modifiers, @NotNull String searchText) {
        return super.processSelectedItem(selected, modifiers, searchText);
    }

    @Override
    public Object getDataForItem(@NotNull Object element, @NotNull String dataId) {
        return null;
    }

    @Override
    public @NotNull String getSearchProviderId() {
        return HttpUrlSearchEverywhereContributor.class.getSimpleName();
    }

    @Override
    public boolean showInFindResults() {
        return false;
    }

    @Override
    public boolean isDumbAware() {
        return DumbService.isDumb(myProject);
    }

    @Override
    public boolean isShownInSeparateTab() {
        return true;
    }


    @Override
    public @NotNull @Nls String getGroupName() {
        return "Debug Tools";
    }


    @Override
    public int getSortWeight() {
        return 999;
    }


}
