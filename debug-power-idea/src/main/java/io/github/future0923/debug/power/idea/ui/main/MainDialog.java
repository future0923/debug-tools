package io.github.future0923.debug.power.idea.ui.main;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import io.github.future0923.debug.power.client.holder.ClientSocketHolder;
import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.exception.SocketCloseException;
import io.github.future0923.debug.power.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.context.MethodDataContext;
import io.github.future0923.debug.power.idea.model.ParamCache;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.ui.JsonEditor;
import io.github.future0923.debug.power.idea.utils.DebugPowerActionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
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

    private final DebugPowerSettingState settingState;

    private MainPanel mainPanel;

    public MainDialog(MethodDataContext methodDataContext, Project project) {
        super(project, true, IdeModalityType.MODELESS);
        this.project = project;
        this.methodDataContext = methodDataContext;
        this.settingState = DebugPowerSettingState.getInstance(project);
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
        String text = DebugPowerJsonUtils.compress(editor.getText());
        Map<String, String> itemHeaderMap = mainPanel.getItemHeaderMap();
        ParamCache paramCacheDto = new ParamCache(itemHeaderMap, text);
        settingState.putMethodParamCache(methodDataContext.getCacheKey(), paramCacheDto);
        Map<String, RunContentDTO> contentMap = DebugPowerJsonUtils.toRunContentDTOMap(text);
        Map<String, String> headers = Stream.of(itemHeaderMap, settingState.getGlobalHeader())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1
                ));
        RunDTO runDTO = new RunDTO();
        runDTO.setHeaders(headers);
        runDTO.setTargetClassName(methodDataContext.getPsiClass().getQualifiedName());
        runDTO.setTargetMethodName(methodDataContext.getPsiMethod().getName());
        runDTO.setTargetMethodParameterTypes(DebugPowerActionUtil.toParamTypeNameList(methodDataContext.getPsiMethod().getParameterList()));
        runDTO.setTargetMethodContent(contentMap);
        runDTO.setRunConfigDTO(settingState.convertRunConfigDTO());
        RunTargetMethodRequestPacket packet = new RunTargetMethodRequestPacket(runDTO);
        if (ClientSocketHolder.INSTANCE == null) {
            Messages.showErrorDialog("Run attach first", "执行失败");
            return;
        }
        try {
            ClientSocketHolder.INSTANCE.send(packet);
        } catch (SocketCloseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.doOKAction();
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }
}
