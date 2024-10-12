package io.github.future0923.debug.power.idea.model;

import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author future0923
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ParamCache {

    private Map<String, String> itemHeaderMap;

    private String paramContent;

    private String xxlJobParam;

    public static final ParamCache NULL = new ParamCache();

    public String formatContent() {
        if (null == paramContent) {
            return null;
        }
        return DebugPowerJsonUtils.pretty(paramContent);
    }
}