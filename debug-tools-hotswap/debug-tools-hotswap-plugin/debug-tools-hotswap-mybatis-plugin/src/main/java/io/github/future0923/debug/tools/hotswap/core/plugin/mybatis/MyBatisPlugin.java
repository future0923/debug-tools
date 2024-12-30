package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnResourceFileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.command.Command;
import io.github.future0923.debug.tools.hotswap.core.command.ReflectionCommand;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtNewMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisRefreshCommands;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.test.InstancesHolder;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer.IbatisTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer.MyBatisEntityTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer.MyBatisMapperTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer.XmlTransformer;
import io.github.future0923.debug.tools.hotswap.core.util.HotswapTransformer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Mybatis热重载插件
 */
@Plugin(name = "MyBatis",
        description = "Reload MyBatis configuration after configuration create/change.",
        testedVersions = {"All between 5.3.2"},
        expectedVersions = {"5.3.2"},
        supportClass = {
                IbatisTransformer.class,
                XmlTransformer.class,
                MyBatisMapperTransformer.class,
                MyBatisEntityTransformer.class
        }
)
public class MyBatisPlugin {

    private static final Logger LOGGER = Logger.getLogger(MyBatisPlugin.class);

    @Init
    Scheduler scheduler;

    @Init
    ClassLoader appClassLoader;

    @Init
    HotswapTransformer hotswapTransformer;

    private final Map<String, Object> configurationMap = new HashMap<>();

    private final Command reloadConfigurationCommand = new ReflectionCommand(this, MyBatisRefreshCommands.class.getName(), "reloadConfiguration");

    @Init
    public void init(PluginConfiguration pluginConfiguration) {
        LOGGER.info("MyBatis plugin initialized.");
    }

    public void registerConfigurationFile(String configFile, Object configObject) {
        if (configFile != null && !configurationMap.containsKey(configFile)) {
            LOGGER.debug("MyBatisPlugin - configuration file registered : {}", configFile);
            configurationMap.put(configFile, configObject);
        }
    }

    @OnResourceFileEvent(path = "/", filter = ".*.xml", events = {FileEvent.MODIFY})
    public void registerResourceListeners(URL url) {
        if (configurationMap.containsKey(url.getPath())) {
            refresh(500);
        }
    }

    @OnClassLoadEvent(
            classNameRegexp = "com.baomidou.mybatisplus.core.MybatisConfiguration",
            events = {LoadEvent.DEFINE}
    )
    public static void transformPlusConfiguration(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod removeMappedStatementMethod = CtNewMethod.make("public void $$removeMappedStatement(String statementName){if(mappedStatements.containsKey(statementName)){mappedStatements.remove(statementName);}}", ctClass);
        ctClass.addMethod(removeMappedStatementMethod);
        ctClass.getDeclaredMethod("addMappedStatement", new CtClass[]{classPool.get("org.apache.ibatis.mapping.MappedStatement")}).insertBefore("$$removeMappedStatement($1.getId());");
    }

    // reload the configuration - schedule a command to run in the application classloader and merge
    // duplicate commands.
    private void refresh(int timeout) {
        scheduler.scheduleCommand(reloadConfigurationCommand, timeout);
    }
}
