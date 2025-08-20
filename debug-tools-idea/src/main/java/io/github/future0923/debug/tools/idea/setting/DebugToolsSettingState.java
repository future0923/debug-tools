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
package io.github.future0923.debug.tools.idea.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.enums.PrintSqlType;
import io.github.future0923.debug.tools.base.hutool.json.JSONUtil;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;
import io.github.future0923.debug.tools.common.dto.TraceMethodDTO;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    /**
     * agent包版本
     */
    private String agentVersion;

    /**
     * agent包路径
     */
    private String agentPath;

    /**
     * 调用方法参数缓存
     */
    private Map<String, String> methodParamCache = new ConcurrentHashMap<>();

    /**
     * 全局请求头信息
     */
    private Map<String, String> globalHeader = new ConcurrentHashMap<>();

    /**
     * 默认参数生成模式
     */
    private GenParamType defaultGenParamType = GenParamType.ALL;

    /**
     * 是否打印SQL
     */
    private PrintSqlType printSql;

    /**
     * 是否附着的本地应用
     */
    private boolean local = true;

    /**
     * 方法上quick debug的可见性
     */
    private Boolean lineMarkerVisible;

    /**
     * 本地的http端口
     */
    private Integer localHttpPort;

    /**
     * 远程应用地址
     */
    private String remoteHost;

    /**
     * 远程应用TCP端口
     */
    private Integer remoteTcpPort;

    /**
     * 远程应用HTTP端口
     */
    private Integer remoteHttpPort;

    /**
     * 匹配URL时移除ContextPath信息
     */
    private String removeContextPath;

    /**
     * 是否开启本地热重载
     */
    private Boolean hotswap = false;

    /**
     * 是否自动附着当前项目启动的应用
     */
    private Boolean autoAttach = false;

    /**
     * 是否保存SQL日志
     */
    private Boolean autoSaveSql = false;

    /**
     * 保存日志天数（天）
     */
    private Integer sqlRetentionDays = 1;

    /**
     * 远程应用名称
     */
    private String remoteName;
    /**
     * 远程应用列表
     */
    private Map<String, String> remoteHosts = new LinkedHashMap<>();

    /**
     * 追踪方法信息
     */
    private TraceMethodDTO traceMethodDTO;

    /**
     * 默认选中的MethodAround名
     */
    private String defaultMethodAroundName;

    /**
     * 方法Around信息
     * key名字，value路径
     */
    private Map<String, String> methodAroundMap = new LinkedHashMap<>();

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

    /**
     * 从string YES | NO 升级为 PrintSqlType，默认为 NO
     */
    public PrintSqlType getPrintSql() {
        if (printSql == null) {
            setPrintSql(PrintSqlType.NO);
        }
        return printSql;
    }

    public String getUrl(String uri) {
        return getUrl(uri, isLocal());
    }

    public String getUrl(String uri, boolean local) {
        return "http://" + (local ? "127.0.0.1" : getRemoteHost()) + ":" + (local ? getLocalHttpPort() : getRemoteHttpPort()) + uri;
    }


    public void saveRemoteHost() {
        Map<String, Object> map = new HashMap<>();
        map.put("host", getRemoteHost());
        map.put("tcpPort", getRemoteTcpPort());
        map.put("httpPort", getRemoteHttpPort());
        String name = StringUtils.isBlank(getRemoteName()) ? getRemoteHost() + "@" + getRemoteTcpPort() + "@" + getRemoteHttpPort() : getRemoteName();
        map.put("name", name);
        getRemoteHosts().put(name, JSONUtil.toJsonStr(map));
    }

    public void delAllHost() {
        getRemoteHosts().clear();
    }

}
