package io.github.future0923.debug.tools.idea.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.action.QuickDebugEditorPopupMenuAction;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.tools.idea.model.ParamCache;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
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
@State(name = "DebugToolsSettingState", storages = @Storage("DebugToolsSettingState.xml"))
public class DebugToolsSettingState implements PersistentStateComponent<DebugToolsSettingState> {

    private static final Logger log = Logger.getInstance(DebugToolsSettingState.class);

    private String agentVersion;

    private Map<String, String> methodParamCache = new ConcurrentHashMap<>();

    private Map<String, String> globalHeader = new ConcurrentHashMap<>();

    private String agentPath;

    private GenParamType defaultGenParamType = GenParamType.ALL;

    private Boolean printSql = false;

    private boolean local;

    private Integer localHttpPort;

    private String remoteHost;

    private Integer remoteTcpPort;

    private Integer remoteHttpPort;

    @Override
    public @Nullable DebugToolsSettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DebugToolsSettingState debugToolsSettingState) {
        XmlSerializerUtil.copyBean(debugToolsSettingState, this);
    }

    public static DebugToolsSettingState getInstance(@NotNull Project project) {
        return project.getService(DebugToolsSettingState.class);
    }

    public void clearMethodParamCache() {
        methodParamCache.clear();
    }

    public void putMethodParamCache(String key, ParamCache value) {
        methodParamCache.put(key, DebugToolsJsonUtils.toJsonStr(value));
    }

    public ParamCache getMethodParamCache(String key) {
        String value = methodParamCache.get(key);
        if (StringUtils.isBlank(value)) {
            return ParamCache.NULL;
        }
        try {
            ParamCache obj = DebugToolsJsonUtils.toBean(value, ParamCache.class);
            if (obj.formatContent() != null) {
                return obj;
            }
        } catch (Exception ignored) {
        }
        return ParamCache.NULL;
    }

    public synchronized String loadAgentPath() {
        return loadAgentPath(null);
    }

    public synchronized String loadAgentPath(Project project) {
        if (ProjectConstants.DEBUG || !ProjectConstants.VERSION.equals(agentVersion) || StringUtils.isBlank(agentPath) || !new File(agentPath).exists()) {
            InputStream inputStream = QuickDebugEditorPopupMenuAction.class.getResourceAsStream(IdeaPluginProjectConstants.AGENT_JAR_PATH);
            if (inputStream == null) {
                DebugToolsNotifierUtil.notifyError(project, "读取代理Jar失败");
                return "";
            }
            try {
                agentPath = DebugToolsFileUtils.getTmpLibFile(inputStream, IdeaPluginProjectConstants.AGENT_TMP_PREFIX + ProjectConstants.VERSION, ".jar").getAbsolutePath();
            } catch (IOException ex) {
                log.error("读取代理Jar失败", ex);
                DebugToolsNotifierUtil.notifyError(project, "读取代理Jar失败");
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
