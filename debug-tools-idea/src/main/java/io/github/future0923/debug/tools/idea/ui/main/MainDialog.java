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

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.utils.DebugToolsDigestUtil;
import io.github.future0923.debug.tools.common.dto.RunContentDTO;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.dto.TraceMethodDTO;
import io.github.future0923.debug.tools.common.exception.SocketCloseException;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.tools.idea.context.MethodDataContext;
import io.github.future0923.debug.tools.idea.model.InvokeMethodRecordDTO;
import io.github.future0923.debug.tools.idea.model.ParamCache;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import io.github.future0923.debug.tools.idea.utils.DebugToolsActionUtil;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIdeaClassUtil;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author future0923
 */
public class MainDialog extends DialogWrapper {

    private static final Logger log = Logger.getInstance(MainDialog.class);

    private final Project project;

    private final MethodDataContext methodDataContext;

    private final DebugToolsSettingState settingState;

    private MainPanel mainPanel;

    public MainDialog(MethodDataContext methodDataContext, Project project) {
        super(project, true, IdeModalityType.MODELESS);
        this.project = project;
        this.methodDataContext = methodDataContext;
        this.settingState = DebugToolsSettingState.getInstance(project);
        setTitle(DebugToolsBundle.message("action.quick.debug.text"));
        setOKButtonText(DebugToolsBundle.message("action.quick.debug.run.text"));
        setOKButtonIcon(AllIcons.Actions.RunAll);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        mainPanel = new MainPanel(project, methodDataContext);
        return mainPanel;
    }

    @Override
    protected void doOKAction() {
        Map<String, String> itemHeaderMap = mainPanel.getItemHeaderMap();
        AllClassLoaderRes.Item classLoaderRes = (AllClassLoaderRes.Item) mainPanel.getClassLoaderComboBox().getSelectedItem();
        MainJsonEditor editor = mainPanel.getEditor();
        String text = DebugToolsJsonUtils.compress(editor.getText());
        String xxlJobParam = mainPanel.getXxlJobParamField().getText();
        TraceMethodPanel traceMethodPanel = mainPanel.getTraceMethodPanel();
        String methodAroundName = (String) mainPanel.getMethodAroundComboBox().getSelectedItem();
        ParamCache paramCacheDto = new ParamCache();
        paramCacheDto.setItemHeaderMap(itemHeaderMap);
        paramCacheDto.setParamContent(text);
        paramCacheDto.setXxlJobParam(xxlJobParam);
        TraceMethodDTO traceMethodDTO = new TraceMethodDTO();
        traceMethodDTO.setTraceMethod(traceMethodPanel.isTraceMethod());
        traceMethodDTO.setTraceMaxDepth(traceMethodPanel.getMaxDepth());
        traceMethodDTO.setTraceMyBatis(traceMethodPanel.isTraceMyBatis());
        traceMethodDTO.setTraceSQL(traceMethodPanel.isTraceSql());
        traceMethodDTO.setTraceSkipStartGetSetCheckBox(traceMethodPanel.isTraceSkipStartGetSetCheckBox());
        traceMethodDTO.setTraceBusinessPackageRegexp(traceMethodPanel.getTraceBusinessPackage());
        traceMethodDTO.setTraceIgnorePackageRegexp(traceMethodPanel.getTraceIgnorePackage());
        paramCacheDto.setTraceMethodDTO(traceMethodDTO);
        paramCacheDto.setMethodAround(methodAroundName);
        settingState.putMethodParamCache(methodDataContext.getCacheKey(), paramCacheDto);
        Map<String, RunContentDTO> contentMap = DebugToolsJsonUtils.toRunContentDTOMap(text);
        Map<String, String> headers = Stream.of(itemHeaderMap, settingState.getGlobalHeader())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1
                ));
        RunDTO runDTO = new RunDTO();
        runDTO.setHeaders(headers);
        runDTO.setClassLoader(classLoaderRes);
        runDTO.setTargetClassName(DebugToolsIdeaClassUtil.tryInnerClassName(methodDataContext.getPsiClass()));
        runDTO.setTargetMethodName(methodDataContext.getPsiMethod().getName());
        runDTO.setTargetMethodParameterTypes(DebugToolsActionUtil.toParamTypeNameList(methodDataContext.getPsiMethod().getParameterList()));
        runDTO.setTargetMethodContent(contentMap);
        runDTO.setXxlJobParam(xxlJobParam);
        runDTO.setTraceMethodDTO(traceMethodDTO);
        if (StrUtil.isNotBlank(methodAroundName)) {
            String filePath = project.getBasePath() + IdeaPluginProjectConstants.METHOD_AROUND_DIR + methodAroundName + ".java";
            if (FileUtil.exist(filePath)) {
                runDTO.setMethodAroundContent(FileUtil.readUtf8String(filePath));
            }
        }
        String identity = DebugToolsDigestUtil.md5(runDTO.toString());
        runDTO.setIdentity(identity);
        RunTargetMethodRequestPacket packet = new RunTargetMethodRequestPacket(runDTO);
        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(project);
        if (info == null) {
            Messages.showErrorDialog(DebugToolsBundle.message("dialog.error.run.attach.first"), DebugToolsBundle.message("dialog.title.execution.failed"));
            DebugToolsToolWindowFactory.showWindow(project, null);
            return;
        }
        try {
            info.getClient().getHolder().send(packet);
        } catch (SocketCloseException e) {
            Messages.showErrorDialog(DebugToolsBundle.message("dialog.error.socket.close"), DebugToolsBundle.message("dialog.title.execution.failed"));
            return;
        } catch (Exception e) {
            Messages.showErrorDialog(DebugToolsBundle.message("dialog.error.socket.send") + e.getMessage(), DebugToolsBundle.message("dialog.title.execution.failed"));
            return;
        }
        try {
            String pathname = project.getBasePath() + IdeaPluginProjectConstants.PARAM_FILE;
            File file = new File(pathname);
            if (!file.exists()) {
                FileUtils.touch(file);
            }
            FileUtil.writeUtf8String(DebugToolsJsonUtils.toJsonStr(runDTO), file);
        } catch (IOException ex) {
            log.error("参数写入json文件失败", ex);
            DebugToolsNotifierUtil.notifyError(project, "参数写入json文件失败");
            return;
        }
        InvokeMethodRecordDTO invokeMethodRecordDTO = new InvokeMethodRecordDTO();
        invokeMethodRecordDTO.setIdentity(identity);
        invokeMethodRecordDTO.formatRunTime();
        invokeMethodRecordDTO.setClassName(runDTO.getTargetClassName());
        invokeMethodRecordDTO.setClassSimpleName(methodDataContext.getPsiClass().getName());
        invokeMethodRecordDTO.setMethodName(runDTO.getTargetMethodName());
        invokeMethodRecordDTO.setMethodSignature(DebugToolsIdeaClassUtil.genMethodSignature(methodDataContext.getPsiMethod()));
        invokeMethodRecordDTO.setMethodAroundName(methodAroundName);
        invokeMethodRecordDTO.setMethodParamJson(text);
        invokeMethodRecordDTO.setCacheKey(methodDataContext.getCacheKey());
        invokeMethodRecordDTO.formatRunDTO(runDTO);
        DebugToolsToolWindowFactory.consumerToolWindow(project, toolWindow -> toolWindow.getInvokeMethodRecordPanel().addItem(invokeMethodRecordDTO));
        super.doOKAction();
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }
    
    @Override
    protected @NotNull Action getCancelAction() {
        Action cancelAction = super.getCancelAction();
        cancelAction.putValue(Action.NAME, DebugToolsBundle.message("action.cancel"));
        return cancelAction;
    }
}
