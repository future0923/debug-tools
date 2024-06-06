package io.github.future0923.debug.power.idea.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import io.github.future0923.debug.power.base.constants.ProjectConstants;
import io.github.future0923.debug.power.base.utils.DebugPowerFileUtils;
import io.github.future0923.debug.power.common.dto.RunConfigDTO;
import io.github.future0923.debug.power.common.enums.PrintResultType;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.action.QuickDebugEditorPopupMenuAction;
import io.github.future0923.debug.power.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.power.idea.model.ParamCache;
import io.github.future0923.debug.power.idea.model.ServerDisplayValue;
import io.github.future0923.debug.power.idea.utils.DebugPowerNotifierUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author future0923
 */
@Getter
@Setter
@State(name = "DebugPowerSettingState", storages = @Storage("DebugPowerSettingState.xml"))
public class DebugPowerSettingState implements PersistentStateComponent<DebugPowerSettingState> {

    private static final Logger log = Logger.getInstance(DebugPowerSettingState.class);

    private String version;

    private Map<String, String> cache = new ConcurrentHashMap<>();

    private ServerDisplayValue attach;

    private String agentPath;

    private Boolean runApplicationAttach = true;

    private PrintResultType printResultType = PrintResultType.JSON;

    private GenParamType defaultGenParamType = GenParamType.ALL;

    private Boolean printSql = false;

    @Override
    public @Nullable DebugPowerSettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DebugPowerSettingState debugPowerSettingState) {
        XmlSerializerUtil.copyBean(debugPowerSettingState, this);
    }

    public static DebugPowerSettingState getInstance(@NotNull Project project) {
        return project.getService(DebugPowerSettingState.class);
    }

    public void clearCache() {
        cache.clear();
    }

    public void putCache(String key, ParamCache value) {
        cache.put(key, DebugPowerJsonUtils.toJsonStr(value));
    }

    public ParamCache getCache(String key) {
        String value = cache.get(key);
        if (StringUtils.isBlank(value)) {
            return ParamCache.NULL;
        }
        try {
            ParamCache obj = DebugPowerJsonUtils.toBean(value, ParamCache.class);
            if (obj.formatContent() != null) {
                return obj;
            }
        } catch (Exception ignored) {
        }
        return ParamCache.NULL;
    }

    public RunConfigDTO convertRunConfigDTO() {
        RunConfigDTO dto = new RunConfigDTO();
        dto.setPrintResultType(printResultType);
        return dto;
    }

    public String getAgentPath() {
        return getAgentPath(null);
    }

    public String getAgentPath(Project project) {
        if (ProjectConstants.DEBUG || !ProjectConstants.VERSION.equals(version) || StringUtils.isBlank(agentPath) || !new File(agentPath).exists()) {
            InputStream inputStream = QuickDebugEditorPopupMenuAction.class.getResourceAsStream(IdeaPluginProjectConstants.AGENT_JAR_PATH);
            if (inputStream == null) {
                DebugPowerNotifierUtil.notifyError(project, "读取代理Jar失败");
                return "";
            }
            try {
                agentPath = DebugPowerFileUtils.getTmpLibFile(inputStream, IdeaPluginProjectConstants.AGENT_TMP_PREFIX + ProjectConstants.VERSION, ".jar").getAbsolutePath();
            } catch (IOException ex) {
                log.error("读取代理Jar失败", ex);
                DebugPowerNotifierUtil.notifyError(project, "读取代理Jar失败");
                return "";
            }
            version = ProjectConstants.VERSION;
        }
        return agentPath;
    }
}
