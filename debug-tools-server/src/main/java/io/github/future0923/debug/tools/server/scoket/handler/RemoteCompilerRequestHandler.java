package io.github.future0923.debug.tools.server.scoket.handler;

import cn.hutool.core.exceptions.ExceptionUtil;
import io.github.future0923.debug.tools.base.classloader.DefaultClassLoader;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsOSUtils;
import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.RemoteCompilerRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RemoteCompilerResponsePacket;
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
public class RemoteCompilerRequestHandler extends BasePacketHandler<RemoteCompilerRequestPacket> {

    private static final Logger logger = Logger.getLogger(RemoteCompilerRequestHandler.class);

    public static final RemoteCompilerRequestHandler INSTANCE = new RemoteCompilerRequestHandler();

    private static final Object hotswapLock = new Object();

    private RemoteCompilerRequestHandler() {

    }

    @Override
    public void handle(OutputStream outputStream, RemoteCompilerRequestPacket packet) throws Exception {
        long start = System.currentTimeMillis();
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
            writeAndFlushNotException(outputStream, RemoteCompilerResponsePacket.of(true, "Hot deploy success, file [" + reloadClass + "]", DebugToolsBootstrap.serverConfig.getApplicationName()));
            return;
        }
        try {
            logger.reload("Reloading classes {}", reloadClass);
            synchronized (hotswapLock) {
                Instrumentation instrumentation = DebugToolsBootstrap.INSTANCE.getInstrumentation();
                instrumentation.redefineClasses(definitions.toArray(new ClassDefinition[0]));
            }
            long end = System.currentTimeMillis();
            logger.reload("reloaded classes {}", reloadClass);
            writeAndFlushNotException(outputStream, RemoteCompilerResponsePacket.of(true, "Hot deploy success. cost " + (end - start) +" ms. file [" + reloadClass + "]", DebugToolsBootstrap.serverConfig.getApplicationName()));
        } catch (Exception e) {
            logger.error("Fail to reload classes {}, msg is {}", reloadClass, e);
            writeAndFlushNotException(outputStream, RemoteCompilerResponsePacket.of(false, "Hot deploy error, file [" + reloadClass + "]\n" + ExceptionUtil.stacktraceToString(e, -1), DebugToolsBootstrap.serverConfig.getApplicationName()));
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
