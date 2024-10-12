package io.github.future0923.debug.power.idea.ui.convert;

import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum ConvertType {

    IMPORT("Import", "Convert", "Import Other Convert to Debug Power Run Json"),

    EXPORT("Export", "Copy", "Export Debug Power Run Json Convert to Other"),
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
