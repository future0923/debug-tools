package io.github.future0923.debug.tools.common.enums;

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

    REQUEST("request"),

    RESPONSE("response"),

    FILE("file"),

    CLASS("class"),

    ;

    private final String type;

    RunContentType(String type) {
        this.type = type;
    }
}
