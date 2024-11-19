package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
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

    public static FileChangedService getInstance(@NotNull Project project) {
        FileChangedService service = project.getService(FileChangedService.class);
        if (service != null && service.lastJavaScanTime == null) {
            service.project = project;
            long now = System.currentTimeMillis();
            service.lastJavaScanTime = now;
            service.lastResourceScanTime = now;
        }
        return service;
    }


    public void clearModifiedJavaFile() {
        this.lastJavaScanTime = System.currentTimeMillis();
    }

    public void clearModifiedResourceFile() {
        this.lastResourceScanTime = System.currentTimeMillis();
    }

    private Set<String> vcsChangedFiles() {
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
            if (this.selectResource) {
                // 如果文件扩展名不是 "class"，则检查映射配置
                if (!"class".equals(virtualFile.getExtension())) {
                    continue; // 如果映射配置为空，继续下一个迭代
                }
            } else {
                // 如果没有选择资源，且文件扩展名不是 "java"，继续下一个迭代
                if (!"java".equals(virtualFile.getExtension())) {
                    continue;
                }
            }
            // 添加路径到集合中
            allChangedFiles.add(virtualFile.getPath());
        }
        return allChangedFiles;
    }

    public Set<String> getModifiedJavaFiles() {
        return this.checkVCS ? vcsChangedFiles() : scanModifiedJavaFiles();
    }

    public Set<String> scanModifiedJavaFiles() {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        final Set<String> modifiedFiles = new HashSet<>();
        for (Module module : modules) {
            final ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
            VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
            for (VirtualFile contentRoot : contentRoots) {
                VirtualFile srcDirectory = contentRoot.findChild("src");
                if (srcDirectory != null && srcDirectory.isDirectory()) {
                    VfsUtilCore.visitChildrenRecursively(srcDirectory, new VirtualFileVisitor<>() {
                        @Override
                        public boolean visitFile(@NotNull VirtualFile file) {
                            if (projectFileIndex.isInTestSourceContent(file)) {
                                return false;
                            } else {
                                if (file.isValid() && file.getTimeStamp() > lastJavaScanTime && "java".equals(file.getExtension())) {
                                    modifiedFiles.add(file.getPath());
                                }
                                return true;
                            }
                        }
                    });
                }
            }
        }
        return modifiedFiles;
    }

    public Set<String> getModifiedResourceFiles() {
        return this.checkVCS ? this.vcsChangedFiles() : this.scanModifiedResourceFiles("");
    }

    public Set<String> scanModifiedResourceFiles(String path) {
        // 创建一个用于存储修改文件路径的集合
        final Set<String> modifiedFiles = new HashSet<>();
        // 获取映射路径对应的虚拟文件夹
        VirtualFile virtualFileDir = VirtualFileUtil.getVirtualFileByPath(path);

        if (virtualFileDir != null) {
            // 遍历文件夹中的所有文件
            VfsUtilCore.visitChildrenRecursively(virtualFileDir, new VirtualFileVisitor<>() {
                @Override
                public boolean visitFile(@NotNull VirtualFile file) {
                    // 确保文件有效
                    if (file.isValid()) {
                        // 如果文件的时间戳大于上次扫描时间，且路径和文件类型匹配，则认为是修改过的文件
                        if (file.getTimeStamp() > lastResourceScanTime) {
                            // 将修改过的文件路径添加到集合中
                            modifiedFiles.add(file.getPath());
                        }
                    }
                    return true;  // 继续遍历
                }
            });
        }
        return modifiedFiles;
    }


}

