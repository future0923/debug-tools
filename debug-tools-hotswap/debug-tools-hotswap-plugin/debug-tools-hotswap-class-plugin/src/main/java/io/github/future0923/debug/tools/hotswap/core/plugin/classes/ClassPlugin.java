package io.github.future0923.debug.tools.hotswap.core.plugin.classes;

import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.classes.transformer.AnonymousClassTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.classes.transformer.ClassInitTransformer;

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


}
