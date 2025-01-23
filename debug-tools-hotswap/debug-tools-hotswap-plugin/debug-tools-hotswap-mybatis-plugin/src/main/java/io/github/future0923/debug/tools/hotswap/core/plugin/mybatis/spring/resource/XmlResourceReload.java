package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.spring.resource;

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

/**
 * 重新载入mybatis的xml资源
 *
 * @author future0923
 */
@SuppressWarnings("unchecked")
public class XmlResourceReload extends AbstractMyBatisResourceReload {

    private static final Logger logger = Logger.getLogger(XmlResourceReload.class);

    public XmlResourceReload(String loadedResource, Configuration configuration) {
        this.type = MyBatisResourceReload.XML_TYPE;
        this.loadedResource = loadedResource;
        this.configuration = configuration;
        this.loadedResources = (Set<String>) ReflectionHelper.get(configuration, LOADED_RESOURCES_FIELD);
    }

    @Override
    public void reload(URL url) throws Exception {
        removeLoadedResource();
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(
                url.openConnection().getInputStream(),
                configuration,
                loadedResource,
                configuration.getSqlFragments()
        );
        try {
            this.removeSelectKey(xmlMapperBuilder);
        } catch (Error error) {
            logger.error("mybatis 重置selectKey失败，url：{}", url);
        }
        xmlMapperBuilder.parse();
    }

    private void removeSelectKey(XMLMapperBuilder xmlMapperBuilder) {
        XPathParser parser = (XPathParser) ReflectionHelper.get(xmlMapperBuilder, "parser");
        XNode xNode = parser.evalNode("/mapper");
        String namespace = xNode.getStringAttribute(NAMESPACE);
        Map<String, KeyGenerator> keyGenerators = (Map<String, KeyGenerator>) ReflectionHelper.get(this.configuration, "keyGenerators");
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
