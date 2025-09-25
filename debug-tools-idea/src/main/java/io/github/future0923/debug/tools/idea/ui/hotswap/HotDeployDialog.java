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
package io.github.future0923.debug.tools.idea.ui.hotswap;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileUtil;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.ui.FormBuilder;
import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.common.exception.SocketCloseException;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.common.protocal.packet.request.LocalCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RemoteCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.ResourceHotDeployRequestPacket;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.client.socket.utils.SocketSendUtils;
import io.github.future0923.debug.tools.idea.listener.data.MulticasterEventPublisher;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIdeaClassUtil;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import io.github.future0923.debug.tools.idea.utils.FileChangedService;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author future0923
 */
public class HotDeployDialog extends DialogWrapper {
    private static final Logger log = Logger.getInstance(HotDeployDialog.class);
    @Getter
    private final DefaultListModel<String> hotUndoShowList;
    private final Project project;
    private final AllClassLoaderRes.Item projectDefaultClassLoader;
    private final List<String> fullPathJavaFiles = new ArrayList<>();
    private final List<String> fullPathResourceFiles = new ArrayList<>();
    private boolean local = true;
    private boolean isJava = true;
    private boolean checkVCS = false;

    public HotDeployDialog(@Nullable Project project, AllClassLoaderRes.Item projectDefaultClassLoader) {
        super(project, true, IdeModalityType.MODELESS);
        this.project = project;
        this.projectDefaultClassLoader = projectDefaultClassLoader;
        this.hotUndoShowList = new DefaultListModel<>();
        this.init();
        this.setTitle(DebugToolsBundle.message("dialog.title.change.files"));
        this.pack();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel compilerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        ButtonGroup compilerButtonGroup = new ButtonGroup();
        JRadioButton localRadio = new JRadioButton(DebugToolsBundle.message("dialog.option.local.intellij"));
        localRadio.setSelected(true);
        localRadio.addItemListener((e) -> local = true);
        JRadioButton remoteRadio = new JRadioButton(DebugToolsBundle.message("dialog.option.remote.attach"));
        remoteRadio.addItemListener((e) -> local = false);
        compilerButtonGroup.add(localRadio);
        compilerButtonGroup.add(remoteRadio);
        compilerPanel.add(localRadio);
        compilerPanel.add(remoteRadio);
        JRadioButton javaRadio = new JRadioButton(DebugToolsBundle.message("dialog.option.java"));
        javaRadio.addActionListener((e) -> {
            if (javaRadio.isSelected()) {
                isJava = true;
                FileChangedService fileChangedBean = FileChangedService.getInstance(this.project);
                fileChangedBean.setSelectResource(false);
                this.fillInJavaFiles();
            }
        });
        JRadioButton resourceRadio = new JRadioButton("Resource");
        resourceRadio.addActionListener((e) -> {
            if (resourceRadio.isSelected()) {
                isJava = false;
                FileChangedService fileChangedBean = FileChangedService.getInstance(this.project);
                fileChangedBean.setSelectResource(true);
                this.fillInResourceFiles();
            }
        });
        JPanel optPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        ButtonGroup group = new ButtonGroup();
        group.add(javaRadio);
        group.add(resourceRadio);
        optPanel.add(javaRadio);
        optPanel.add(resourceRadio);
        JCheckBox vcs = new JCheckBox(DebugToolsBundle.message("dialog.option.vcs"));
        vcs.addActionListener((e) -> {
            FileChangedService fileChangedBean = FileChangedService.getInstance(project);
            checkVCS = vcs.isSelected();
            fileChangedBean.setCheckVCS(checkVCS);
            this.fullPathResourceFiles.clear();
            this.fullPathJavaFiles.clear();
            if (this.isResource()) {
                this.fillInResourceFiles();
            } else {
                this.fillInJavaFiles();
            }
        });
        optPanel.add(vcs);
        final JBList<String> jbList = new JBList<>(this.hotUndoShowList);
        jbList.setCellRenderer(new ColoredListCellRenderer<String>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends String> list, String value, int index, boolean selected, boolean hasFocus) {
                if (!FileUtil.isAbsolutePath(value)) {
                    append(value, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.BLUE));
                } else {
                    append(value);
                }
            }
        });
        JBScrollPane scrollPane = new JBScrollPane(jbList);
        Dimension minimumSize = new Dimension(950, 400);
        scrollPane.setMinimumSize(minimumSize);
        FileChangedService fileChangedBean = FileChangedService.getInstance(this.project);
        if (fileChangedBean.isCheckVCS()) {
            vcs.setSelected(true);
        }
        if (fileChangedBean.isSelectResource()) {
            resourceRadio.setSelected(true);
            this.fillInResourceFiles();
        } else {
            javaRadio.setSelected(true);
            this.fillInJavaFiles();
        }
        MulticasterEventPublisher publisher = new MulticasterEventPublisher();
        HotDeployToolBar mainToolBar = new HotDeployToolBar(publisher);
        publisher.addListener(new AddListener(project, this));
        publisher.addListener(new DeleteListener(this, jbList));
        publisher.addListener(new ClearListener(this));
        publisher.addListener(new ResetListener(this));
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        formBuilder.addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("dialog.compiler.type")),
                        compilerPanel
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("dialog.select.type")),
                        optPanel
                )
                .addComponent(mainToolBar)
                .addComponent(scrollPane);
        return formBuilder.getPanel();
    }

    @Override
    protected void doOKAction() {
        List<VirtualFile> virtualFiles = getHotUndoList().stream().map(LocalFileSystem.getInstance()::findFileByPath).collect(Collectors.toList());
        if (CollUtil.isEmpty(virtualFiles)) {
            Messages.showErrorDialog(DebugToolsBundle.message("dialog.empty.content.error"), DebugToolsBundle.message("dialog.title.execution.failed"));
            return;
        }
        if (isJava) {
            if (local) {
                localCompiler(virtualFiles);
            } else {
                remoteCompiler(virtualFiles);
            }
        } else {
            ResourceHotDeployRequestPacket hotSwapRequestPacket = new ResourceHotDeployRequestPacket();
            for (VirtualFile virtualFile : virtualFiles) {
                if (virtualFile == null) {
                    continue;
                }
                VirtualFile sourceRootForFile = ProjectFileIndex.getInstance(project).getSourceRootForFile(virtualFile);
                if (sourceRootForFile != null) {
                    String filePath = VfsUtilCore.getRelativePath(virtualFile, sourceRootForFile, '/');
                    try {
                        byte[] bytes = virtualFile.contentsToByteArray();
                        hotSwapRequestPacket.add(filePath, bytes);
                    } catch (IOException ignored) {

                    }
                }
            }
            hotSwapRequestPacket.setIdentity(projectDefaultClassLoader.getIdentity());
            SocketSendUtils.send(project, hotSwapRequestPacket);
        }
        super.doOKAction();
    }

    private void remoteCompiler(List<VirtualFile> virtualFiles) {
        // 编译成功后，更新文件并上传
        ReadAction.nonBlocking(() -> {
            RemoteCompilerHotDeployRequestPacket packet = new RemoteCompilerHotDeployRequestPacket();
            for (VirtualFile virtualFile : virtualFiles) {
                String content = VirtualFileUtil.readText(virtualFile);
                // 获取源文件路径和内容
                String packageName = DebugToolsIdeaClassUtil.getPackageName(content);
                if (packageName == null) {
                    return null;
                }
                String packetAllName = packageName + "." + virtualFile.getName().replace(".java", "");
                packet.add(packetAllName, content);
                packet.setIdentity(projectDefaultClassLoader.getIdentity());
            }
            SocketSendUtils.sendThrowException(project, packet);
            return null;
        }).submit(AppExecutorUtil.getAppExecutorService())
        .onError(throwable -> {
            if (throwable instanceof NullPointerException) {
                DebugToolsNotifierUtil.notifyError(project, DebugToolsBundle.message("error.run.attach.first"));
                DebugToolsToolWindowFactory.showWindow(project, null);
                return;
            }
            if (throwable instanceof SocketCloseException) {
                DebugToolsNotifierUtil.notifyError(project, DebugToolsBundle.message("dialog.error.socket.close"));
                DebugToolsToolWindowFactory.showWindow(project, null);
                return;
            }
            Messages.showErrorDialog("Socket send error " + throwable.getMessage(), "Send Error");
            DebugToolsToolWindowFactory.showWindow(project, null);
        });
    }

    private void localCompiler(List<VirtualFile> virtualFiles) {
        // 获取编译管理器实例
        CompilerManager compilerManager = CompilerManager.getInstance(project);
        // 创建编译范围
        CompileScope scope = compilerManager.createFilesCompileScope(virtualFiles.toArray(new VirtualFile[0]));
        // 编译这些文件
        compilerManager.compile(scope, (aborted, errors, warnings, compileContext) -> {
            if (errors > 0) {
                // 记录错误日志
                log.error("Compilation failed with errors: " + errors + ", warnings: " + warnings);
                return;
            }
            // 编译成功后，更新文件并上传
            ReadAction.nonBlocking(() -> {
                Set<VirtualFile> allOutputs = new HashSet<>();
                List<ClassFilePath> allOutputClasses = new ArrayList<>();
                // 遍历所有虚拟文件
                virtualFiles.forEach((selectedFile) -> {
                    // 获取输出目录
                    VirtualFile outputDirectory = compileContext.getModuleOutputDirectory(compileContext.getModuleByFile(selectedFile));
                    if (outputDirectory == null) {
                        log.error(DebugToolsBundle.message("dialog.error.output.directory"));
                        return;
                    }
                    // 如果当前输出目录已经收集过，跳过此目录
                    if (allOutputs.contains(outputDirectory)) {
                        return;
                    }
                    allOutputs.add(outputDirectory);
                    // 获取源文件路径和内容
                    String sourceFilePath = selectedFile.getPath();
                    Document document = FileDocumentManager.getInstance().getDocument(selectedFile);
                    String packageName = DebugToolsIdeaClassUtil.getPackageName(document.getText());
                    if (packageName == null) {
                        log.error(DebugToolsBundle.message("dialog.error.parse.package"));
                        return;
                    }
                    String className = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1).replace(".java", "");
                    // 构建源文件的基本名称
                    String sourceFileBaseName = packageName.replace(".", "/") + "/" + className;
                    // 收集编译后的类文件
                    List<ClassFilePath> outputClasses = collectClassFiles(outputDirectory, sourceFileBaseName);
                    // 如果未找到类文件，再次刷新输出目录并尝试收集
                    if (outputClasses.isEmpty()) {
                        outputDirectory.refresh(false, true);
                        outputClasses = collectClassFiles(outputDirectory, sourceFileBaseName);
                    }
                    allOutputClasses.addAll(outputClasses);
                });
                if (CollUtil.isEmpty(allOutputClasses)) {
                    DebugToolsNotifierUtil.notifyError(project, DebugToolsBundle.message("dialog.error.local.compiler.error"));
                    return null;
                }
                LocalCompilerHotDeployRequestPacket hotSwapRequestPacket = new LocalCompilerHotDeployRequestPacket();
                for (ClassFilePath allOutputClass : allOutputClasses) {
                    hotSwapRequestPacket.add(allOutputClass.getClassName(), allOutputClass.getPayload());
                }
                hotSwapRequestPacket.setIdentity(projectDefaultClassLoader.getIdentity());
                SocketSendUtils.sendThrowException(project, hotSwapRequestPacket);
                return null;
            })
            .submit(AppExecutorUtil.getAppExecutorService())
            .onError(throwable -> {
                if (throwable instanceof NullPointerException) {
                    DebugToolsNotifierUtil.notifyError(project, DebugToolsBundle.message("error.run.attach.first"));
                    DebugToolsToolWindowFactory.showWindow(project, null);
                    return;
                }
                if (throwable instanceof SocketCloseException) {
                    DebugToolsNotifierUtil.notifyError(project, DebugToolsBundle.message("dialog.error.socket.close"));
                    DebugToolsToolWindowFactory.showWindow(project, null);
                    return;
                }
                Messages.showErrorDialog("Socket send error " + throwable.getMessage(), "Send Error");
                DebugToolsToolWindowFactory.showWindow(project, null);
            });
        });
    }

    private static List<ClassFilePath> collectClassFiles(VirtualFile directory, final String sourceFileBaseName) {
        // 创建一个空的列表 outPutClasses，用于存储匹配的 ClassFilePath 对象
        final List<ClassFilePath> outPutClasses = new ArrayList<>();

        // 使用 VfsUtilCore 递归访问目录中的所有文件
        VfsUtilCore.visitChildrenRecursively(directory, new VirtualFileVisitor<>() {
            @Override
            public boolean visitFile(@NotNull VirtualFile file) {
                // 检查文件是否为 .class 文件，并且路径与 sourceFileBaseName 匹配
                if (file.getPath().endsWith(".class")
                        && isMatchPathClass(file.getPath(), sourceFileBaseName)) {
                    // 创建新的 ClassFilePath 对象并设置路径和类名
                    ClassFilePath classFilePath = new ClassFilePath();
                    classFilePath.setFullPath(file.getPath());
                    classFilePath.setPayload(FileUtil.readBytes(classFilePath.getFullPath()));
                    String className = file.getPath()
                            .substring(file.getPath().lastIndexOf(sourceFileBaseName), file.getPath().length() - ".class".length())
                            .replace("/", ".");
                    classFilePath.setClassName(className);
                    // 将 ClassFilePath 对象添加到列表中
                    outPutClasses.add(classFilePath);
                }
                // 返回 true 继续递归访问其他文件
                return true;
            }
        });

        // 返回收集到的 ClassFilePath 对象列表
        return outPutClasses;
    }

    public static boolean isMatchPathClass(String fullPath, String classnamePath) {
        Pattern pattern = Pattern.compile(classnamePath + "(\\$\\w+)?\\.class");
        Matcher matcher = pattern.matcher(fullPath);
        return matcher.find();
    }

    public void fillInJavaFiles() {
        // 清空 hotUndoShowList 中的内容
        this.hotUndoShowList.clear();
        // 如果 fullPathJavaFiles 为空，则获取已修改的 Java 文件
        if (this.fullPathJavaFiles.isEmpty()) {
            // 获取文件变更管理实例
            FileChangedService fileChangedBean = FileChangedService.getInstance(this.project);
            // 获取已修改的 Java 文件集合，将已修改的文件路径添加到 fullPathJavaFiles
            fileChangedBean.getModifiedJavaFiles().onSuccess(files -> {
                this.fullPathJavaFiles.addAll(files);
                // 获取项目的基础路径
                String basePath = this.project.getBasePath();
                // 如果 basePath 不为空且不以斜杠结尾，则添加斜杠
                if (basePath != null && !basePath.endsWith("/")) {
                    basePath = basePath + "/";
                }
                // 遍历 fullPathJavaFiles 中的所有 Java 文件路径
                for (String filePath : this.fullPathJavaFiles) {
                    // 将每个文件路径存入 relaPath
                    String relaPath = filePath;
                    // 如果 basePath 不为 null，则从路径中去除 basePath 部分
                    if (basePath != null) {
                        relaPath = relaPath.replace(basePath, "");
                    }
                    // 将修改后的路径添加到 hotUndoShowList
                    this.hotUndoShowList.addElement(relaPath);
                }
            });
        }
    }

    public void fillInResourceFiles() {
        // 清空 hotUndoShowList 中的内容
        this.hotUndoShowList.clear();
        // 如果 fullPathResourceFiles 为空，则获取已修改的资源文件
        if (this.fullPathResourceFiles.isEmpty()) {
            // 获取文件变更工具实例
            FileChangedService fileChangedBean = FileChangedService.getInstance(this.project);
            // 获取已修改的资源文件集合, 将修改的资源文件路径添加到 fullPathResourceFiles
            fileChangedBean.getModifiedResourceFiles().onSuccess(files -> {
                this.fullPathResourceFiles.addAll(files);
                // 获取项目的基础路径
                String basePath = this.project.getBasePath();
                // 如果基础路径不为空且不以斜杠结尾，则添加斜杠
                if (basePath != null && !basePath.endsWith("/")) {
                    basePath = basePath + "/";
                }
                // 遍历 fullPathResourceFiles 中的每个文件路径
                for (String filePath : this.fullPathResourceFiles) {
                    // 初始化 relaPath 为当前文件路径
                    String relaPath = filePath;
                    // 如果基础路径不为空，则从文件路径中去除基础路径部分，得到相对路径
                    if (basePath != null) {
                        relaPath = relaPath.replace(basePath, "");
                    }
                    // 将相对路径添加到 hotUndoShowList 中
                    this.hotUndoShowList.addElement(relaPath);
                }
            });
        }
    }

    public boolean isResource() {
        FileChangedService fileChangedBean = FileChangedService.getInstance(this.project);
        return fileChangedBean.isSelectResource();
    }

    public List<String> getHotUndoList() {
        return this.isResource() ? this.fullPathResourceFiles : this.fullPathJavaFiles;
    }

    @Data
    public static class ClassFilePath {
        private String className;
        private String fullPath;
        private byte[] payload;
    }

    @Data
    public static class ResourceFilePath {
        private String filePath;
        private byte[] payload;
    }

    public void reset() {
        FileChangedService instance = FileChangedService.getInstance(project);
        instance.setSelectResource(!isJava);
        instance.setCheckVCS(checkVCS);
        if (isJava) {
            fullPathJavaFiles.clear();
            fillInJavaFiles();
        } else {
            fullPathResourceFiles.clear();
            fillInResourceFiles();
        }
    }
}
