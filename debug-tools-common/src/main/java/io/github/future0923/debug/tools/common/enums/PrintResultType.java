package io.github.future0923.debug.tools.common.enums;

import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum PrintResultType {

    NO_PRINT("NoPrint"),

    TOSTRING("ToString"),

    JSON("Json"),

    DEBUG("Debug"),
    ;

    private final String type;

    PrintResultType(String type) {
        this.type = type;
    }
}
