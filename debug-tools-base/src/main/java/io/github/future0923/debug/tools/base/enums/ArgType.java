/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.base.enums;

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
