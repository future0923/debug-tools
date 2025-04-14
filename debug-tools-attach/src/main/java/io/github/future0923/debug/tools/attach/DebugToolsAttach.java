package io.github.future0923.debug.tools.attach;

import io.github.future0923.debug.tools.base.classloader.DefaultClassLoader;
import io.github.future0923.debug.tools.base.config.AgentArgs;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.HotswapAgent;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.URLClassLoaderPathHelper;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.sql.SqlPrintByteCodeEnhance;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author future0923
 */
public class DebugToolsAttach {

    private static final Logger logger = Logger.getLogger(DebugToolsAttach.class);

    private static final AtomicBoolean isStarted = new AtomicBoolean(false);

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        if (ProjectConstants.DEBUG) {
            CtClass.debugDump = "debug/javassist";
            System.setProperty("cglib.debugLocation", "debug/cglib");
        }
        AgentArgs parse = AgentArgs.parse(agentArgs);
        if (parse.getLogLevel() != null) {
            Logger.setLevel(parse.getLogLevel());
        }
        if (Objects.equals(parse.getPrintSql(), "true")) {
            SqlPrintByteCodeEnhance.enhance(inst);
        }
        if (Objects.equals(parse.getHotswap(), "true")) {
            HotswapAgent.init(parse, inst);
        }
    }

    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        if (!isStarted.compareAndSet(false, true)) {
            return;
        }
        DebugToolsBootstrap.getInstance(inst).start(agentArgs);
        isStarted.set(true);
    }
}