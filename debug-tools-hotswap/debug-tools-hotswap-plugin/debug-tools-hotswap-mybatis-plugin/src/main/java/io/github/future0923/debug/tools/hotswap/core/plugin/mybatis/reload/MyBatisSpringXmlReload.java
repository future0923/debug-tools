/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
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

    private static final Set<String> RELOADING_XML = ConcurrentHashMap.newKeySet();

    private MyBatisSpringXmlReload() {

    }

    @Override
    protected void doReload(URL url) throws Exception {
        String loadedResource = buildLoadedResource(url);
        String path = url.getPath();
        if (!RELOADING_XML.add(path)) {
            if (ProjectConstants.DEBUG) {
                logger.info("{} is currently processing reload task.", path);
            }
            return;
        }
        try {
            for (Configuration configuration : MyBatisSpringResourceManager.getConfigurationList()) {
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
                logger.reload("reload MyBatis xml file {}", path);
            }
        } catch (Exception e) {
            logger.error("refresh mybatis xml error", e);
        } finally {
            RELOADING_XML.remove(path);
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
