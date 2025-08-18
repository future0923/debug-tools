package io.github.future0923.debug.tools.idea.bundle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class DebugToolsBundle {

    public static final String BUNDLE = "messages.DebugToolsBundle";

    @NotNull
    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return CustomDebugToolsBundle.message(key, params);
    }
}