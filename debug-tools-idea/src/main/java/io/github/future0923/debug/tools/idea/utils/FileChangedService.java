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
package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.util.concurrency.AppExecutorUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.CancellablePromise;
import org.jetbrains.jps.model.java.JavaResourceRootType;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author future0923
 */
public class FileChangedService {

    private Project project;

    private Long lastJavaScanTime;

    private Long lastResourceScanTime;

    @Setter
    @Getter
    private boolean selectResource;
    @Setter
    @Getter
    private boolean checkVCS;

    private FileChangedService() {

    }

    public static FileChangedService getInstance(@NotNull Project project) {
        FileChangedService service = project.getService(FileChangedService.class);
        if (service != null) {
            service.project = project;
            Long projectOpenTime = StateUtils.getProjectOpenTime(project);
            service.lastJavaScanTime = projectOpenTime;
            service.lastResourceScanTime = projectOpenTime;
        }
        return service;
    }


    public void clearModifiedJavaFile() {
        this.lastJavaScanTime = System.currentTimeMillis();
    }

    public void clearModifiedResourceFile() {
        this.lastResourceScanTime = System.currentTimeMillis();
    }

    private CancellablePromise<Set<String>> vcsChangedFiles() {
        return ReadAction.nonBlocking(() -> {
            ChangeListManager changeListManager = ChangeListManager.getInstance(this.project);
            Collection<Change> changes = changeListManager.getAllChanges();
            Set<String> allChangedFiles = new HashSet<>();
            for (Change change : changes) {
                VirtualFile virtualFile = change.getVirtualFile();
                // 跳过 null 或无效的文件
                if (virtualFile == null || !virtualFile.isValid()) {
                    continue;
                }
                // 处理选择资源的逻辑
                JpsModuleSourceRootType<?> sourceRootType = ProjectFileIndex.getInstance(project).getContainingSourceRootType(virtualFile);
                if (this.selectResource) {
                    if (ObjectUtil.equal(sourceRootType, JavaResourceRootType.RESOURCE)) {
                        allChangedFiles.add(virtualFile.getPath());
                    }
                } else {
                    if (ObjectUtil.equal(sourceRootType, JavaSourceRootType.SOURCE) && "java".equals(virtualFile.getExtension())) {
                        allChangedFiles.add(virtualFile.getPath());
                    }
                }
            }
            return allChangedFiles;
        }).submit(AppExecutorUtil.getAppExecutorService());
    }

    public CancellablePromise<Set<String>> getModifiedJavaFiles() {
        return this.checkVCS ? vcsChangedFiles() : scanModifiedJavaFiles();
    }

    public CancellablePromise<Set<String>> scanModifiedJavaFiles() {
        return ReadAction.nonBlocking(() -> {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            final Set<String> modifiedFiles = new HashSet<>();
            for (Module module : modules) {
                List<VirtualFile> sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots(JavaSourceRootType.SOURCE);
                for (VirtualFile contentRoot : sourceRoots) {
                    if (contentRoot != null && contentRoot.isDirectory()) {
                        VfsUtilCore.visitChildrenRecursively(contentRoot, new VirtualFileVisitor<>() {
                            @Override
                            public boolean visitFile(@NotNull VirtualFile virtualFile) {
                                JpsModuleSourceRootType<?> sourceRootType = ProjectFileIndex.getInstance(project).getContainingSourceRootType(virtualFile);
                                if (virtualFile.isValid() && virtualFile.getTimeStamp() > lastJavaScanTime
                                        && ObjectUtil.equal(sourceRootType, JavaSourceRootType.SOURCE)
                                        && "java".equals(virtualFile.getExtension())) {
                                    modifiedFiles.add(virtualFile.getPath());
                                }
                                return true;
                            }
                        });
                    }
                }
            }
            return modifiedFiles;
        }).submit(AppExecutorUtil.getAppExecutorService());
    }

    public CancellablePromise<Set<String>> getModifiedResourceFiles() {
        return this.checkVCS ? this.vcsChangedFiles() : this.scanModifiedResourceFiles();
    }

    public CancellablePromise<Set<String>> scanModifiedResourceFiles() {
        return ReadAction.nonBlocking(() -> {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            final Set<String> modifiedFiles = new HashSet<>();
            for (Module module : modules) {
                List<VirtualFile> sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots(JavaResourceRootType.RESOURCE);
                for (VirtualFile contentRoot : sourceRoots) {
                    if (contentRoot != null && contentRoot.isDirectory()) {
                        VfsUtilCore.visitChildrenRecursively(contentRoot, new VirtualFileVisitor<>() {
                            @Override
                            public boolean visitFile(@NotNull VirtualFile virtualFile) {
                                JpsModuleSourceRootType<?> sourceRootType = ProjectFileIndex.getInstance(project).getContainingSourceRootType(virtualFile);
                                if (virtualFile.isValid() && virtualFile.getTimeStamp() > lastResourceScanTime
                                        && ObjectUtil.equal(sourceRootType, JavaResourceRootType.RESOURCE)) {
                                    modifiedFiles.add(virtualFile.getPath());
                                }
                                // 继续遍历
                                return true;
                            }
                        });
                    }
                }
            }
            return modifiedFiles;
        }).submit(AppExecutorUtil.getAppExecutorService());
    }
}
