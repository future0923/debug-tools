package io.github.future0923.debug.power.common.enums;

import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum RunContentType {

    UNKNOWN("unknown"),

    SIMPLE("simple"),

    ENUM("enum"),

    JSON_ENTITY("json_entity"),

    LAMBDA("lambda"),

    BEAN("bean"),

    ;

    private final String type;

    RunContentType(String type) {
        this.type = type;
    }
}
