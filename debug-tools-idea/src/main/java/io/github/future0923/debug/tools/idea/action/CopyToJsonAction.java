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
package io.github.future0923.debug.tools.idea.action;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.debugger.engine.SuspendContext;
import com.intellij.debugger.engine.evaluation.EvaluationContext;
import com.intellij.debugger.ui.impl.watch.ValueDescriptorImpl;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.sun.jdi.*;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.StringSelection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author caoayu
 */
public class CopyToJsonAction extends AnAction {

    private static final Logger logger = Logger.getLogger(CopyToJsonAction.class);

    public CopyToJsonAction() {
        getTemplatePresentation().setText(DebugToolsBundle.message("action.copy.to.json.text"));
        getTemplatePresentation().setDescription(DebugToolsBundle.message("action.copy.to.json.description"));
        getTemplatePresentation().setIcon(DebugToolsIcons.Json);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        List<XValueNodeImpl> selectedNodes = XDebuggerTree.getSelectedNodes(e.getDataContext());
        if (selectedNodes.isEmpty() || selectedNodes.size() > 1) return;
        XValueNodeImpl node = selectedNodes.get(0);
        if (!(node.getValueContainer() instanceof JavaValue javaValue)) return;
        Value jdiVal = javaValue.getDescriptor().getValue();
        if (!(jdiVal instanceof ObjectReference objRef)) return;

        try {
            EvaluationContext evalContext = ((ValueDescriptorImpl) javaValue.getDescriptor()).getStoredEvaluationContext();
            SuspendContext suspendContext = Objects.requireNonNull(evalContext.getSuspendContext());
            ThreadReference thread = Objects.requireNonNull(suspendContext.getThread()).getThreadReference();
            if (thread == null || !thread.isSuspended()) {
                Messages.showErrorDialog(project, DebugToolsBundle.message("action.copy.to.json.thread.error"), DebugToolsBundle.message("action.copy.to.json.thread.error.title"));
                return;
            }

            // --- 查找并使用序列化器 ---
            ProjectJsonSerializer serializer = findSerializer(objRef.virtualMachine());

            if (serializer != null) {
                String json = serializer.serialize(objRef, thread);
                copyToClipboard(project, json, DebugToolsBundle.message("action.copy.to.json.success", serializer.getLibraryName()));
            } else {
                Messages.showWarningDialog(project, DebugToolsBundle.message("action.copy.to.json.library.not.found"), DebugToolsBundle.message("action.copy.to.json.library.not.found.title"));
            }

        } catch (Exception ex) {
            logger.error(DebugToolsBundle.message("action.copy.to.json.error", ex.getMessage()), ex);
            Messages.showErrorDialog(project, DebugToolsBundle.message("action.copy.to.json.error", ex.getMessage()), DebugToolsBundle.message("action.copy.to.json.error.title"));
        }
    }

    /**
     * 按“Hutool -> Fastjson -> Jackson”的优先级查找序列化器。
     */
    @Nullable
    private ProjectJsonSerializer findSerializer(VirtualMachine vm) {
        // 优先级 1: Hutool (静态)
        List<ReferenceType> hutoolClasses = vm.classesByName("cn.hutool.json.JSONUtil");
        if (!hutoolClasses.isEmpty()) {
            logger.info("Hutool JSON found.");
            return new HutoolSerializer(hutoolClasses.get(0));
        }

        // 优先级 2: Fastjson/Fastjson2 (静态)
        List<ReferenceType> fastjsonClasses = vm.classesByName("com.alibaba.fastjson.JSON");
        if (fastjsonClasses.isEmpty()) {
            fastjsonClasses = vm.classesByName("com.alibaba.fastjson2.JSON");
        }
        if (!fastjsonClasses.isEmpty()) {
            logger.info("Fastjson(or 2) found.");
            return new FastjsonSerializer(fastjsonClasses.get(0));
        }

        // 优先级 3 (兜底): Jackson (new 实例并手动配置)
        List<ReferenceType> jacksonClasses = vm.classesByName("com.fasterxml.jackson.databind.ObjectMapper");
        if (!jacksonClasses.isEmpty() && jacksonClasses.get(0) instanceof ClassType) {
            logger.info("Jackson found. Will use new instance with manual config.");
            return new JacksonSerializer((ClassType) jacksonClasses.get(0));
        }

        logger.warning("No supported JSON library found.");
        return null;
    }


    // ================================================================================================
    //                     序列化器接口与实现
    // ================================================================================================
    private interface ProjectJsonSerializer {
        String serialize(ObjectReference object, ThreadReference thread) throws Exception;
        String getLibraryName();
    }

    private static class HutoolSerializer implements ProjectJsonSerializer {
        private final ReferenceType jsonUtilClass;
        public HutoolSerializer(ReferenceType clz) { this.jsonUtilClass = clz; }
        @Override
        public String serialize(ObjectReference object, ThreadReference thread) throws Exception {
            Method method = jsonUtilClass.methodsByName("toJsonPrettyStr", "(Ljava/lang/Object;)Ljava/lang/String;").get(0);
            StringReference result = (StringReference) ((ClassType)jsonUtilClass).invokeMethod(thread, method, Collections.singletonList(object), ObjectReference.INVOKE_SINGLE_THREADED);
            return result.value();
        }
        @Override
        public String getLibraryName() { return "Hutool JSON"; }
    }

    private static class FastjsonSerializer implements ProjectJsonSerializer {
        private final ReferenceType jsonClass;
        public FastjsonSerializer(ReferenceType clz) { this.jsonClass = clz; }
        @Override
        public String serialize(ObjectReference object, ThreadReference thread) throws Exception {
            Method method = jsonClass.methodsByName("toJSONString", "(Ljava/lang/Object;)Ljava/lang/String;").get(0);
            StringReference result = (StringReference) ((ClassType)jsonClass).invokeMethod(thread, method, Collections.singletonList(object), ObjectReference.INVOKE_SINGLE_THREADED);
            return result.value();
        }
        @Override
        public String getLibraryName() { return "Fastjson"; }
    }

    private static class JacksonSerializer implements ProjectJsonSerializer {
        private final ClassType objectMapperClass;
        public JacksonSerializer(ClassType clz) { this.objectMapperClass = clz; }
        @Override
        public String serialize(ObjectReference object, ThreadReference thread) throws Exception {
            Method ctor = objectMapperClass.methodsByName("<init>", "()V").get(0);
            ObjectReference objectMapperInstance = objectMapperClass.newInstance(thread, ctor, Collections.emptyList(), ObjectReference.INVOKE_SINGLE_THREADED);
            try {
                configureJavaTimeModule(objectMapperInstance, thread);
                logger.info(DebugToolsBundle.message("action.copy.to.json.jackson.config.success"));
            } catch (Exception e) {
                logger.warning(DebugToolsBundle.message("action.copy.to.json.jackson.config.warning", e.getMessage()));
            }
            Method serializeMethod = objectMapperClass.methodsByName("writeValueAsString", "(Ljava/lang/Object;)Ljava/lang/String;").get(0);
            StringReference jsonResult = (StringReference) objectMapperInstance.invokeMethod(thread, serializeMethod, Collections.singletonList(object), ObjectReference.INVOKE_SINGLE_THREADED);
            return jsonResult.value();
        }
        private void configureJavaTimeModule(ObjectReference objectMapperInstance, ThreadReference thread) throws Exception {
            VirtualMachine vm = objectMapperInstance.virtualMachine();
            List<ReferenceType> moduleClasses = vm.classesByName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule");
            if (moduleClasses.isEmpty() || !(moduleClasses.get(0) instanceof ClassType)) throw new ClassNotFoundException("JavaTimeModule not found.");
            ClassType moduleClass = (ClassType) moduleClasses.get(0);
            Method moduleCtor = moduleClass.methodsByName("<init>", "()V").get(0);
            ObjectReference moduleInstance = moduleClass.newInstance(thread, moduleCtor, Collections.emptyList(), ObjectReference.INVOKE_SINGLE_THREADED);
            Method registerMethod = objectMapperClass.methodsByName("registerModule", "(Lcom/fasterxml/jackson/databind/Module;)Lcom/fasterxml/jackson/databind/ObjectMapper;").get(0);
            objectMapperInstance.invokeMethod(thread, registerMethod, Collections.singletonList(moduleInstance), ObjectReference.INVOKE_SINGLE_THREADED);
        }
        @Override
        public String getLibraryName() { return "Jackson (with manual config)"; }
    }


    // --- 通用辅助方法 ---
    private void copyToClipboard(Project project, String content, String title) {
        CopyPasteManager.getInstance().setContents(new StringSelection(content));
        NotificationGroupManager.getInstance()
                .getNotificationGroup("DebugTools")
                .createNotification(title, content, NotificationType.INFORMATION)
                .notify(project);
    }
    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        e.getPresentation().setEnabledAndVisible(XDebuggerManager.getInstance(project).getCurrentSession() != null);
    }
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}