package io.github.future0923.debug.power.base.enums;

import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum ArgType {

    TCP_PORT("tp", "tcp-port", true, "target application server tcp port\ndefault get available port."),
    HTTP_PORT("hp", "http-port", true, "target application server http port\ndefault get available port."),
    ;

    private final String opt;

    private final String longOpt;

    private final boolean hasArg;

    private final String description;

    ArgType(String opt, String longOpt, boolean hasArg, String description) {
        this.opt = opt;
        this.longOpt = longOpt;
        this.hasArg = hasArg;
        this.description = description;
    }
}
