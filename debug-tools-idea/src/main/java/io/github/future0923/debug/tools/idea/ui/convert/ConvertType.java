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
package io.github.future0923.debug.tools.idea.ui.convert;

import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum ConvertType {

    IMPORT("Import", "Convert", "Import Other Convert to Debug Tools Run Json"),

    EXPORT("Export", "Copy", "Export Debug Tools Run Json Convert to Other"),
    ;
    private final String title;

    private final String okButtonText;

    private final String description;

    ConvertType(String title, String okButtonText, String description) {
        this.title = title;
        this.okButtonText = okButtonText;
        this.description = description;
    }
}
