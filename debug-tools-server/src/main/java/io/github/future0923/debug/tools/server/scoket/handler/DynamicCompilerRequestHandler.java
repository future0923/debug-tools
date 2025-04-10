package io.github.future0923.debug.tools.server.scoket.handler;

import cn.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.classloader.DefaultClassLoader;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsOSUtils;
import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.DynamicCompilerRequestPacket;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderHelper;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.compiler.DynamicCompiler;

import java.io.File;
import java.io.OutputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
public class DynamicCompilerRequestHandler extends BasePacketHandler<DynamicCompilerRequestPacket> {

    private static final Logger logger = Logger.getLogger(DynamicCompilerRequestHandler.class);

    public static final DynamicCompilerRequestHandler INSTANCE = new DynamicCompilerRequestHandler();

    private static final Object hotswapLock = new Object();

    private DynamicCompilerRequestHandler() {

    }

    @Override
    public void handle(OutputStream outputStream, DynamicCompilerRequestPacket packet) throws Exception {
        ClassLoader defaultClassLoader = DefaultClassLoader.getDefaultClassLoader();
        DynamicCompiler compiler = new DynamicCompiler(defaultClassLoader);
        packet.getFilePathByteCodeMap().forEach(compiler::addSource);
        Map<String, byte[]> byteCodesMap = compiler.buildByteCodes();
        writeFile(byteCodesMap);
        List<ClassDefinition> definitions = new ArrayList<>();
        for (Map.Entry<String, byte[]> entry : byteCodesMap.entrySet()) {
            if (ClassLoaderHelper.isClassLoaded(defaultClassLoader, entry.getKey())) {
                definitions.add(new ClassDefinition(defaultClassLoader.loadClass(entry.getKey()), entry.getValue()));
            }
        }
        String reloadClass = String.join(", ", byteCodesMap.keySet());
        if (definitions.isEmpty()) {
            logger.warning("There are no classes that need to be redefined. {}", reloadClass);
            return;
        }
        try {
            logger.reload("Reloading classes {}", reloadClass);
            synchronized (hotswapLock) {
                Instrumentation instrumentation = DebugToolsBootstrap.INSTANCE.getInstrumentation();
                instrumentation.redefineClasses(definitions.toArray(new ClassDefinition[0]));
            }
            logger.reload("reloaded classes {}", reloadClass);
        } catch (Exception e) {
            logger.error("Fail to reload classes {}, msg is {}", reloadClass, e);
        }
    }

    private void writeFile(Map<String, byte[]> byteCodesMap) {
        PluginConfiguration pluginConfiguration = PluginManager.getInstance().getPluginConfiguration(DefaultClassLoader.getDefaultClassLoader());
        if (pluginConfiguration == null) {
            logger.error("Failure to retrieve PluginConfiguration. Please ensure that the project is started in hot reload mode.");
            return;
        }
        URL[] classpath = pluginConfiguration.getExtraClasspath();
        if (classpath == null || classpath.length == 0) {
            logger.error("{} is null", DebugToolsOSUtils.isWindows() ? "extraClasspathWin" : "extraClasspath");
            return;
        }
        String extraClasspath = classpath[0].getPath();
        if (!extraClasspath.endsWith(File.separator)) {
            extraClasspath += File.separator;
        }
        for (Map.Entry<String, byte[]> entry : byteCodesMap.entrySet()) {
            String clasFilePath = entry.getKey().replace(".", File.separator).concat(".class");
            DebugToolsFileUtils.writeBytes(entry.getValue(), extraClasspath + clasFilePath);
        }
    }
}
