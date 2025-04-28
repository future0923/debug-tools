package io.github.future0923.debug.tools.base.config;

import io.github.future0923.debug.tools.base.SpyAPI;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * debug-tools-cache.properties配置类
 *
 * @author future0923
 */
public class AgentConfig {

    private static final Logger logger = Logger.getLogger(AgentConfig.class);

    private static final String FILE_NAME = "debug-tools-cache.properties";

    private static final String VERSION = "debug.tools.version";

    private static final String SPRING_EXTENSION_PATH = "debug.tools.extension.spring.path";

    private static final String XXLJOB_EXTENSION_PATH = "debug.tools.extension.xxljob.path";

    private static final String JNI_LIBRARY_PATH = "debug.tools.jni.library.path";

    public static final AgentConfig INSTANCE = new AgentConfig();

    private final Properties properties = new Properties();

    private final File propertiesFile;

    private AgentConfig() {
        String homeDir = System.getProperty("user.home");
        propertiesFile = new File(homeDir + File.separator + ProjectConstants.NAME + File.separator + FILE_NAME);
        if (!propertiesFile.exists()) {
            DebugToolsFileUtils.touch(propertiesFile);
        }
        try {
            properties.load(propertiesFile.toURI().toURL().openStream());
            createExtensionJar();
        } catch (IOException e) {
            logger.error("load properties error", e);
        }

    }

    private void createExtensionJar() {
        String version = getVersion();
        boolean isUpgrade = !ProjectConstants.VERSION.equals(version);
        if (isUpgrade) {
            setVersion(ProjectConstants.VERSION);
        }
        createSpringJar(isUpgrade);
        createXxlJobJar(isUpgrade);
        store();
    }

    private void createSpringJar(boolean isUpgrade) {
        File jarFile = loadJarFile(getSpringExtensionPath(), ProjectConstants.SPRING_EXTENSION_JAR_NAME, isUpgrade);
        setSpringExtensionPath(jarFile.getAbsolutePath());
    }

    private void createXxlJobJar(boolean isUpgrade) {
        File jarFile = loadJarFile(getXxlJobExtensionPath(), ProjectConstants.XXMLJOB_EXTENSION_JAR_NAME, isUpgrade);
        setXxlJobExtensionPath(jarFile.getAbsolutePath());
    }

    private File loadJarFile(String jarPath, String jarName, boolean isUpgrade) {
        File jarFile;
        if (ProjectConstants.DEBUG || jarPath == null || jarPath.isEmpty() || isUpgrade) {
            jarFile = DebugToolsFileUtils.getLibResourceJar(SpyAPI.class.getClassLoader(), jarName);
        } else {
            File file = new File(jarPath);
            if (file.exists()) {
                jarFile = file;
            } else {
                jarFile = DebugToolsFileUtils.getLibResourceJar(SpyAPI.class.getClassLoader(), jarName);
            }
        }
        return jarFile;
    }

    public String getVersion() {
        return properties.getProperty(VERSION);
    }

    public void setVersion(String version) {
        properties.setProperty(VERSION, version);
    }

    public void setVersionAndStore(String version) {
        setVersion(version);
        store();
    }

    public String getSpringExtensionPath() {
        return properties.getProperty(SPRING_EXTENSION_PATH);
    }

    public void setSpringExtensionPath(String path) {
        properties.setProperty(SPRING_EXTENSION_PATH, path);
    }

    public void setSpringExtensionPathAndStore(String corePath) {
        setSpringExtensionPath(corePath);
        store();
    }

    public String getXxlJobExtensionPath() {
        return properties.getProperty(XXLJOB_EXTENSION_PATH);
    }

    public void setXxlJobExtensionPath(String path) {
        properties.setProperty(XXLJOB_EXTENSION_PATH, path);
    }

    public void setXxlJobExtensionPathAndStore(String corePath) {
        setXxlJobExtensionPath(corePath);
        store();
    }

    public String getJniLibraryPath() {
        return properties.getProperty(JNI_LIBRARY_PATH);
    }

    public void setJniLibraryPath(String jniLibraryPath) {
        properties.setProperty(JNI_LIBRARY_PATH, jniLibraryPath);
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
