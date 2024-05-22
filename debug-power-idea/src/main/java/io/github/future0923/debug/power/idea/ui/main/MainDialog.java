package io.github.future0923.debug.power.idea.ui.main;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.utils.DebugPowerFileUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.action.QuickDebugEditorPopupMenuAction;
import io.github.future0923.debug.power.idea.constant.ProjectConstant;
import io.github.future0923.debug.power.idea.context.MethodDataContext;
import io.github.future0923.debug.power.idea.model.ParamCache;
import io.github.future0923.debug.power.idea.model.ServerDisplayValue;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.ui.JsonEditor;
import io.github.future0923.debug.power.idea.utils.DebugPowerActionUtil;
import io.github.future0923.debug.power.idea.utils.DebugPowerAttachUtils;
import io.github.future0923.debug.power.idea.utils.DebugPowerNotifierUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author future0923
 */
public class MainDialog extends DialogWrapper {

    private static final Logger log = Logger.getInstance(MainDialog.class);

    private final Project project;

    private final MethodDataContext methodDataContext;

    private MainPanel mainPanel;

    public MainDialog(MethodDataContext methodDataContext, @Nullable Project project) {
        super(project, true, IdeModalityType.MODELESS);
        this.project = project;
        this.methodDataContext = methodDataContext;
        setTitle("Quick Debug");
        setOKButtonText("Run");
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
        JsonEditor editor = mainPanel.getEditor();
        String auth = mainPanel.getAuthField().getText();
        String text = DebugPowerJsonUtils.compress(editor.getText());
        DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
        if (StringUtils.isBlank(settingState.getAgentPath()) || !new File(settingState.getAgentPath()).exists()) {
            InputStream inputStream = QuickDebugEditorPopupMenuAction.class.getResourceAsStream(ProjectConstant.AGENT_JAR_PATH);
            if (inputStream == null) {
                DebugPowerNotifierUtil.notifyError(project, "读取代理Jar失败");
                return;
            }
            try {
                settingState.setAgentPath(DebugPowerFileUtils.getTmpLibFile(inputStream, "agent", ".jar"));
            } catch (IOException ex) {
                log.error("读取代理Jar失败", ex);
                DebugPowerNotifierUtil.notifyError(project, "读取代理Jar失败");
                return;
            }
        }
        ParamCache paramCacheDto = new ParamCache(text);
        settingState.putCache(methodDataContext.getCacheKey(), paramCacheDto);
        Map<String, String> headers = null;
        if (StringUtil.isNotEmpty(auth)) {
            headers = new HashMap<>();
            headers.put("Authorization", auth);
            settingState.putHeader("Authorization", auth);
        }
        Map<String, RunContentDTO> contentMap = DebugPowerJsonUtils.toRunContentDTOMap(text);
        String jsonDtoStr = DebugPowerJsonUtils.toDebugPowerJson(
                methodDataContext.getPsiClass().getQualifiedName(),
                methodDataContext.getPsiMethod().getName(),
                DebugPowerActionUtil.toParamTypeNameList(methodDataContext.getPsiMethod().getParameterList()),
                contentMap,
                headers,
                settingState.convertRunConfigDTO()
        );
        ServerDisplayValue attach = settingState.getAttach();
        if (attach == null || StringUtil.isEmpty(attach.getKey())) {
            Messages.showErrorDialog("Run attach first", "执行失败");
            return;
        }
        String agentParam;
        try {
            String pathname = project.getBasePath() + ProjectConstant.PARAM_FILE;
            File file = new File(pathname);
            if (!file.exists()) {
                FileUtils.touch(file);
            }
            FileUtil.writeToFile(file, jsonDtoStr);
            agentParam = "file://" + URLEncoder.encode(pathname, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            log.error("参数写入json文件失败", ex);
            DebugPowerNotifierUtil.notifyError(project, "参数写入json文件失败");
            return;
        }
        DebugPowerAttachUtils.attach(project, attach.getKey(), settingState.getAgentPath(), agentParam);
        super.doOKAction();
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }
}
