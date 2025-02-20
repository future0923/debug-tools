package io.github.future0923.debug.tools.hotswap.core.plugin.classes;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.classes.transformer.AnonymousClassTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.classes.transformer.ClassInitTransformer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author future0923
 */
@Plugin(
        name = "Class",
        testedVersions = {"DCEVM"},
        supportClass = {
                AnonymousClassTransformer.class,
                ClassInitTransformer.class
        }
)
public class ClassPlugin {

    private static final Logger logger = Logger.getLogger(ClassPlugin.class);

    private final Map<Class<?>, byte[]> reloadMap = new HashMap<>();

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE, skipSynthetic = false)
    public static void debug(final Class<?> clazz) {
        if (ProjectConstants.DEBUG) {
            logger.reload("redefine class {}", clazz.getName());
        }
    }

}
