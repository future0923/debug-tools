package io.github.future0923.debug.tools.base.config;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.constants.PropertiesConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * @author future0923
 */
public class AgentConfig {

    private static final Logger logger = Logger.getLogger(AgentConfig.class);

    public static final AgentConfig INSTANCE = new AgentConfig();

    private final Properties properties = new Properties();

    private final File propertiesFile;

    private AgentConfig() {
        String homeDir = System.getProperty("user.home");
        propertiesFile = new File(homeDir + "/" + ProjectConstants.NAME + "/debug-tools-cache.properties");
        if (!propertiesFile.exists()) {
            DebugToolsFileUtils.touch(propertiesFile);
        }
        try {
            properties.load(propertiesFile.toURI().toURL().openStream());
        } catch (IOException e) {
            logger.error("load properties error", e);
        }

    }

    public String getVersion() {
        return properties.getProperty(PropertiesConstants.VERSION);
    }

    public void setVersion(String version) {
        properties.setProperty(PropertiesConstants.VERSION, version);
    }

    public void setVersionAndStore(String version) {
        setVersion(version);
        store();
    }

    public String getCorePath() {
        return properties.getProperty(PropertiesConstants.CORE_PATH);
    }

    public void setCorePath(String corePath) {
        properties.setProperty(PropertiesConstants.CORE_PATH, corePath);
    }

    public void setCorePathAndStore(String corePath) {
        setCorePath(corePath);
        store();
    }

    public String getJniLibraryPath() {
        return properties.getProperty(PropertiesConstants.JNI_LIBRARY_PATH);
    }

    public void setJniLibraryPath(String jniLibraryPath) {
        properties.setProperty(PropertiesConstants.JNI_LIBRARY_PATH, jniLibraryPath);
    }

    public void setJniLibraryPathAndStore(String jniLibraryPath) {
        setJniLibraryPath(jniLibraryPath);
        store();
    }

    public void store() {
        try {
            OutputStream outputStream = Files.newOutputStream(propertiesFile.toPath());
            properties.store(outputStream, "config debug tools core path");
            outputStream.close();
        } catch (IOException e) {
            logger.error("store properties error", e);
        }
    }
}
