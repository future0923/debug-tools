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
