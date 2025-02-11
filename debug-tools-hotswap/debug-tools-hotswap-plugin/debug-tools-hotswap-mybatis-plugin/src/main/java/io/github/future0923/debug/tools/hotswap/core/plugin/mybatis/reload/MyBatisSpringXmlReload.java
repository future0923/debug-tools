package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 重新载入mybatis spring的xml资源
 *
 * @author future0923
 */
@SuppressWarnings("unchecked")
public class MyBatisSpringXmlReload extends AbstractMyBatisResourceReload<URL> {

    private static final Logger logger = Logger.getLogger(MyBatisSpringXmlReload.class);

    public static final MyBatisSpringXmlReload INSTANCE = new MyBatisSpringXmlReload();

    private MyBatisSpringXmlReload() {

    }

    @Override
    protected void doReload(URL url) throws Exception {
        String loadedResource = buildLoadedResource(url);
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
            logger.reload("reload MyBatis xml file {}", url.getPath());
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
