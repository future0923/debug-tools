package io.github.future0923.debug.power.idea.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.model.ParamCache;
import io.github.future0923.debug.power.idea.model.ServerDisplayValue;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
@State(name = "DebugPowerSettingState", storages = @Storage("DebugPowerSettingState.xml"))
public class DebugPowerSettingState implements PersistentStateComponent<DebugPowerSettingState> {

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private ServerDisplayValue attach;

    @Getter
    private final Map<String, String> headers = new ConcurrentHashMap<>();

    public String dataFileDir = "/.idea/debug-power-data/";

    @Getter
    @Setter
    public String agentPath;

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

    public void putHeader(String key, String value) {
        headers.put(key, value);
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
}
