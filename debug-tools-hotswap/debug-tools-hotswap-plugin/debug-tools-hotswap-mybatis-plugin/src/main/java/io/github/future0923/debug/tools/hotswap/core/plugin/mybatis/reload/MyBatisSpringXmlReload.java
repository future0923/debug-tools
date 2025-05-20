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
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.utils.MyBatisUtils;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 重新载入mybatis spring的xml资源
 *
 * @author future0923
 */
@SuppressWarnings("unchecked")
public class MyBatisSpringXmlReload extends AbstractMyBatisResourceReload<URL> {

    private static final Logger logger = Logger.getLogger(MyBatisSpringXmlReload.class);

    public static final MyBatisSpringXmlReload INSTANCE = new MyBatisSpringXmlReload();

    private static final Set<String> RELOADING_XML = ConcurrentHashMap.newKeySet();

    private MyBatisSpringXmlReload() {

    }

    @Override
    protected void doReload(URL url) throws Exception {
        String loadedResource = buildLoadedResource(url);
        String path = url.getPath();
        if (RELOADING_XML.contains(path)) {
            if (ProjectConstants.DEBUG) {
                logger.info("{} is currently processing reload task.", path);
            }
            return;
        }
        for (Configuration configuration : MyBatisSpringResourceManager.getConfigurationList()) {
            synchronized (MyBatisUtils.getReloadLockObject()) {
                if (!RELOADING_XML.add(path)) {
                    if (ProjectConstants.DEBUG) {
                        logger.info("{} is currently processing reload task.", path);
                    }
                    return;
                }
                Set<String> loadedResources = (Set<String>) ReflectionHelper.get(configuration, LOADED_RESOURCES_FIELD);
                loadedResources.remove(loadedResource);
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(
                        url.openConnection().getInputStream(),
                        configuration,
                        loadedResource,
                        configuration.getSqlFragments()
                );
                try {
                    this.removeSelectKey(xmlMapperBuilder, configuration);
                } catch (Error error) {
                    logger.error("mybatis 重置selectKey失败，url：{}", url);
                }
                xmlMapperBuilder.parse();
                RELOADING_XML.remove(path);
            }
            logger.reload("reload MyBatis xml file {}", path);
        }
    }

    private String buildLoadedResource(URL url) {
        return FILE + " [" + MyBatisSpringResourceManager.getRelativePath(url) + "]";
    }

    private void removeSelectKey(XMLMapperBuilder xmlMapperBuilder, Configuration configuration) {
        XPathParser parser = (XPathParser) ReflectionHelper.get(xmlMapperBuilder, "parser");
        XNode xNode = parser.evalNode("/mapper");
        String namespace = xNode.getStringAttribute(NAMESPACE);
        Map<String, KeyGenerator> keyGenerators = (Map<String, KeyGenerator>) ReflectionHelper.get(configuration, "keyGenerators");
        if (keyGenerators != null) {
            Iterator<Map.Entry<String, KeyGenerator>> iterator = keyGenerators.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, KeyGenerator> next = iterator.next();
                String key = next.getKey();
                if (key.startsWith(namespace) && key.endsWith("!selectKey")) {
                    iterator.remove();
                }
            }
        }
    }

}
