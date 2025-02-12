package io.github.future0923.debug.tools.hotswap.core.plugin.classes;

import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.classes.transformer.AnonymousClassTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.classes.transformer.ClassEnumTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.classes.transformer.ClassStaticTransformer;

/**
 * @author future0923
 */
@Plugin(
        name = "Class",
        testedVersions = {"DCEVM"},
        supportClass = {
                AnonymousClassTransformer.class,
                ClassStaticTransformer.class,
                ClassEnumTransformer.class
        }
)
public class ClassPlugin {


}
