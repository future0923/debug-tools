package io.github.future0923.debug.power.idea.tool.action;

import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum ClearCacheType {

    CORE_JAR("Core jar cache"),
    METHOD_PARAM("Method param cache"),
    GLOBAL_HEADER("Global header"),
    ;

    private final String type;

    ClearCacheType(String type) {
        this.type = type;
    }
}
