/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnResourceFileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisSpringXmlReloadCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch.DynamicPatcher;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch.IBatisPatcher;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch.MyBatisPlusPatcher;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch.MyBatisSpringPatcher;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.utils.MyBatisUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
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
                IBatisPatcher.class,
                MyBatisPlusPatcher.class,
                MyBatisSpringPatcher.class,
                DynamicPatcher.class
        }
)
public class MyBatisPlugin {

    private static final Logger logger = Logger.getLogger(MyBatisPlugin.class);

    @Init
    Scheduler scheduler;

    @Init
    static ClassLoader appClassLoader;

    /**
     * 不能使用注解，因为注解只能获取AppClassLoader
     */
    private static ClassLoader userClassLoader;

    private final Map<String, Object> configurationMap = new HashMap<>();

    /**
     * 打印Solon信息
     */
    public void init(ClassLoader classLoader) {
        MyBatisPlugin.userClassLoader = classLoader;
    }

    public static ClassLoader getUserClassLoader() {
        return userClassLoader == null ? appClassLoader : userClassLoader;
    }

    /**
     * 在{@link IBatisPatcher#patchXMLMapperBuilder}处调用时生成mapper文件信息
     */
    public void registerConfigurationFile(String configFile, Object configObject) {
        if (configFile != null && !configurationMap.containsKey(configFile)) {
            logger.debug("MyBatisPlugin - configuration file registered : {}", configFile);
            configurationMap.put(configFile, configObject);
        }
    }

    /**
     * OnResourceFileEvent只能在主插件作用与实例对象，所以放在这里
     */
    @OnResourceFileEvent(path = "/", filter = ".*.xml", events = {FileEvent.CREATE, FileEvent.MODIFY})
    public void watchResource(final URL url, final FileEvent fileEvent) throws ParserConfigurationException, IOException, SAXException {
        logger.debug("registerResourceListeners, url:{}", url.getPath());
        if (!MyBatisUtils.isMyBatisSpring(appClassLoader) && !MyBatisUtils.isMyBatisPlus(appClassLoader)) {
            return;
        }
        if ((FileEvent.CREATE.equals(fileEvent) && MyBatisUtils.isMapperXml(url.getPath()))
                || ((FileEvent.MODIFY.equals(fileEvent) && configurationMap.containsKey(url.getPath())))) {
            scheduler.scheduleCommand(new MyBatisSpringXmlReloadCommand(appClassLoader, url), 1000);
        }
    }
}