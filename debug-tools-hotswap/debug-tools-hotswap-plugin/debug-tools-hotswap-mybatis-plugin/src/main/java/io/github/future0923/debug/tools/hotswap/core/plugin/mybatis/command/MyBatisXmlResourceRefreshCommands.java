package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.spring.resource.MyBatisSpringResourceManager;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.spring.resource.XmlResourceReload;

import java.net.URL;
import java.util.List;

/**
 * @author future0923
 */
public class MyBatisXmlResourceRefreshCommands {

    private static final Logger logger = Logger.getLogger(MyBatisXmlResourceRefreshCommands.class);

    public static void reloadConfiguration(URL url) {
        try {
            List<XmlResourceReload> mybatisResource = MyBatisSpringResourceManager.findXmlResource(url);
            if (mybatisResource == null) {
                logger.info("变更 XML 不是 Mybatis XML, xmlPath：{}", url.getPath());
                return;
            }

            for(XmlResourceReload xmlResource : mybatisResource) {
                logger.info("xmlResource objectClassLoader:{}, classLoader:{}", mybatisResource.getClass().getClassLoader(), XmlResourceReload.class.getClassLoader());
                logger.info("Mybatis XML 形式热加载" + url.getPath(), new Object[0]);
                xmlResource.reload(url);
            }
        } catch (Exception e) {
            logger.error("reloadConfiguration error", e);
        }

    }
}
