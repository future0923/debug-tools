package io.github.future0923.debug.tools.common.enums;

import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum ResultClassType {

    VOID("Void"),

    NULL("Null"),

    SIMPLE("Simple"),

    OBJECT("Object"),
    ;

    private final String type;

    ResultClassType(String type) {
        this.type = type;
    }
}
