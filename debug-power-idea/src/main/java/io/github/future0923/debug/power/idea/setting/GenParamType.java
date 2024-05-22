package io.github.future0923.debug.power.idea.setting;

import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum GenParamType {

    SIMPLE("Simple"),
    CURRENT("Current"),
    ALL("All"),
    ;

    private final String type;

    GenParamType(String type) {
        this.type = type;
    }
}
