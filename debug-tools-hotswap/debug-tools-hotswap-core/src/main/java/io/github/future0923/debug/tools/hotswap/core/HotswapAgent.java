package io.github.future0923.debug.tools.hotswap.core;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import lombok.Getter;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Set;

/**
 * 热重载Agent逻辑
 *
 * @author future0923
 */
public class HotswapAgent {

    private static final Logger LOGGER = Logger.getLogger(HotswapAgent.class);

    /**
     * 禁用的插件名
     */
    private static final Set<String> disabledPlugins = new HashSet<>();

    /**
     * 是否自动热重载
     */
    @Getter
    private static boolean autoHotswap = false;

    /**
     * 外部配置文件 `hotswap-agent.properties` 的路径
     */
    private static String propertiesFilePath;

    private static boolean init = false;

    public static void agentmain(String args, Instrumentation inst) {
        premain(args, inst);
    }

    public static void premain(String args, Instrumentation inst) {
        if (!init) {
            LOGGER.info("Loading Hotswap agent {{}} - unlimited runtime class redefinition.", ProjectConstants.VERSION);
            parseArgs(args);
            fixJboss7Modules();
            PluginManager.getInstance().init(inst);
            LOGGER.debug("Hotswap agent initialized.");
            init = true;
        }
    }

    public static void parseArgs(String args) {
        if (args == null)
            return;

        for (String arg : args.split(",")) {
            String[] val = arg.split("=");
            if (val.length != 2) {
                LOGGER.warning("Invalid javaagent command line argument '{}'. Argument is ignored.", arg);
            }

            String option = val[0];
            String optionValue = val[1];

            if ("disablePlugin".equals(option)) {
                disabledPlugins.add(optionValue.toLowerCase());
            } else if ("autoHotswap".equals(option)) {
                autoHotswap = Boolean.parseBoolean(optionValue);
            } else if ("propertiesFilePath".equals(option)) {
                propertiesFilePath = optionValue;
            } else {
                if (!"hotconf".equals(option)) {
                    LOGGER.warning("Invalid javaagent option '{}'. Argument '{}' is ignored.", option, arg);
                }
            }
        }
    }

    /**
     * 获取外部配置文件路径
     */
    public static String getExternalPropertiesFile() {
        return propertiesFilePath;
    }


    /**
     * 插件是否被禁用
     */
    public static boolean isPluginDisabled(String pluginName) {
        return disabledPlugins.contains(pluginName);
    }

    /**
     * 定义允许哪些 Java 包可以从 JBoss 模块系统的类加载器传递到应用程序类加载器中。
     * 它指定了一些系统包可以被 JBoss 的模块化类加载器识别和访问，而不是仅限于默认的类加载范围。
     */
    private static void fixJboss7Modules() {
        String JBOSS_SYSTEM_MODULES_KEY = "jboss.modules.system.pkgs";

        String oldValue = System.getProperty(JBOSS_SYSTEM_MODULES_KEY, null);
        System.setProperty(JBOSS_SYSTEM_MODULES_KEY, oldValue == null ? HOTSWAP_AGENT_EXPORT_PACKAGES : oldValue + "," + HOTSWAP_AGENT_EXPORT_PACKAGES);
    }

    public static final String HOTSWAP_AGENT_EXPORT_PACKAGES =
            "io.github.future0923.debug.tools.attach.hotswap.annotation,"
                    + "io.github.future0923.debug.tools.attach.hotswap.command,"
                    + "io.github.future0923.debug.tools.attach.hotswap.config,"
                    + "io.github.future0923.debug.tools.attach.hotswap.logging,"
                    + "io.github.future0923.debug.tools.attach.hotswap.plugin,"
                    + "io.github.future0923.debug.tools.attach.hotswap.util,"
                    + "io.github.future0923.debug.tools.attach.hotswap.watch,"
                    + "io.github.future0923.debug.tools.attach.hotswap.versions,"
                    + "io.github.future0923.debug.tools.hotswap.core.javassist";
}
