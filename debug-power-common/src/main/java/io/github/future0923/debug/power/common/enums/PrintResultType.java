package io.github.future0923.debug.power.common.enums;

import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum PrintResultType {

    NO_PRINT("NoPrint"),

    TOSTRING("ToString"),

    JSON("Json"),
    ;

    private final String type;

    PrintResultType(String type) {
        this.type = type;
    }
}
