package io.github.future0923.debug.power.attach.config;

import io.github.future0923.debug.power.base.logging.Logger;

import java.lang.instrument.Instrumentation;

/**
 * @author future0923
 */
public class PluginManager {

    private static final Logger logger = Logger.getLogger(PluginManager.class);

    private static final PluginManager INSTANCE = new PluginManager();

    private Instrumentation instrumentation;

    private PluginManager() {

    }

    public static PluginManager getInstance() {
        return INSTANCE;
    }

    public void init(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }
}
