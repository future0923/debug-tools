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

/**
 * @author future0923
 */
@Getter
@Setter
@State(name = "DebugPowerSettingState", storages = @Storage("DebugPowerSettingState.xml"))
public class DebugPowerSettingState implements PersistentStateComponent<DebugPowerSettingState> {

    private static final Logger log = Logger.getInstance(DebugPowerSettingState.class);

    private String agentVersion;

    private Map<String, String> methodParamCache = new ConcurrentHashMap<>();

    private Map<String, String> globalHeader = new ConcurrentHashMap<>();

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

    public void clearMethodParamCache() {
        methodParamCache.clear();
    }

    public void putMethodParamCache(String key, ParamCache value) {
        methodParamCache.put(key, DebugPowerJsonUtils.toJsonStr(value));
    }

    public ParamCache getMethodParamCache(String key) {
        String value = methodParamCache.get(key);
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

    public synchronized String loadAgentPath() {
        return loadAgentPath(null);
    }

    public synchronized String loadAgentPath(Project project) {
        if (ProjectConstants.DEBUG || !ProjectConstants.VERSION.equals(agentVersion) || StringUtils.isBlank(agentPath) || !new File(agentPath).exists()) {
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
            agentVersion = ProjectConstants.VERSION;
        }
        return agentPath;
    }

    public void clearAgentCache() {
        try {
            if (this.agentPath == null) {
                return;
            }
            File file = new File(this.agentPath);
            if (file.exists()) {
                file.delete();
            }
            String homeDir = System.getProperty("user.home");
            File cacheProperties = new File(homeDir + "/" + ProjectConstants.NAME + "/" + ProjectConstants.CONFIG_FILE);
            if (cacheProperties.exists()) {
                cacheProperties.delete();
            }
        } catch (Exception ignored) {
        }
        this.agentPath = null;
    }

    public void putGlobalHeader(String key, String value) {
        if (StringUtils.isNotBlank(key)) {
            globalHeader.put(key, value);
        }
    }

    public void clearGlobalHeaderCache() {
        globalHeader.clear();
    }

    public void clearAllCache() {
        clearAgentCache();
        clearMethodParamCache();
        clearGlobalHeaderCache();
    }
}
