package io.github.future0923.debug.power.idea.model;

import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author future0923
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ParamCache {

    private String content;

    public static final ParamCache NULL = new ParamCache();

    public String formatContent() {
        if (null == content) {
            return null;
        }
        return DebugPowerJsonUtils.pretty(content);
    }
}