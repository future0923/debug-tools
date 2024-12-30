package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnResourceFileEvent;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.test.InstancesHolder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

/**
 * @author future0923
 */
public class XmlTransformer {

    private final Logger logger = Logger.getLogger(XmlTransformer.class);

    @OnResourceFileEvent(path="/", filter = ".*.xml", events = {FileEvent.MODIFY})
    public void patchXMLMapperBuilder(URI uri)     {
        try {
            byte[] bytes = IOUtils.toByteArray(uri);
            if(!new String(bytes).contains("<mapper")) {
                return;
            }
            String path = uri.getPath();
            String fileName = path.substring(path.indexOf("/") + 1);
            Set<Configuration> configurations = InstancesHolder.getInstances(Configuration.class);
            for(Configuration configuration : configurations) {
                try{
                    //清理loadedResources
                    clearLoadResource(configuration.getClass(),configuration,"loadedResources",fileName);
                    //重新编译加载资源文件
                    InputStream inputStream = new ByteArrayInputStream(bytes);
                    XMLMapperBuilder configBuilder = new XMLMapperBuilder(inputStream, configuration, path, configuration.getSqlFragments());
                    configBuilder.parse();
                }catch (Exception e) {
                    logger.error("clearLoadResource:"+e.getMessage());
                }
            }
        } catch (Throwable e) {
            logger.error("clearLoadResource err:",e);
        }
    }

    @SuppressWarnings("rawtypes")
    private void clearLoadResource(Class classConfig, Configuration configuration, String fieldName,String xmlName) throws Exception {
        Field field;
        try{
            field = classConfig.getDeclaredField(fieldName);
        }catch (Exception e) {
            field = Configuration.class.getDeclaredField(fieldName);
        }
        String simpleFileName = xmlName.replace("\\","/");
        if(simpleFileName.contains("/")) {
            simpleFileName = xmlName.substring(xmlName.lastIndexOf("/"));
        }
        field.setAccessible(true);
        Set<String> setConfig = (Set<String>) field.get(configuration);
        Iterator<String> iterator = setConfig.iterator();
        while(iterator.hasNext()){
            String res = iterator.next();
            if(res.contains(".xml") && res.contains(simpleFileName)) {
                iterator.remove();
            }
        }
    }
}
