package io.github.future0923.debug.power.idea.model;

import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author future0923
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamCache {

    private String content;

    public String content() {
        if (null == content) {
            return null;
        }
        return DebugPowerJsonUtils.pretty(content);
    }
}