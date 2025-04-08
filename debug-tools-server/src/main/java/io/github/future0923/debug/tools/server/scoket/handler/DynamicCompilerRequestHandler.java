package io.github.future0923.debug.tools.server.scoket.handler;

import io.github.future0923.debug.tools.base.classloader.DefaultClassLoader;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.DynamicCompilerRequestPacket;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.compiler.DynamicClassLoader;
import io.github.future0923.debug.tools.server.compiler.DynamicCompiler;

import java.io.OutputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.HashMap;
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
        DynamicCompiler compiler = new DynamicCompiler(DefaultClassLoader.getDefaultClassLoader());
        packet.getFilePathByteCodeMap().forEach(compiler::addSource);
        compiler.build();
        DynamicClassLoader classLoader = compiler.getClassLoader();
        Map<String, byte[]> byteCodesMap = classLoader.getByteCodes();
        Map<String, Class<?>> classesMap = classLoader.getClasses();
        Map<Class<?>, byte[]> reloadMap = new HashMap<>();
        packet.getFilePathByteCodeMap().forEach((k, v) -> {
            reloadMap.put(classesMap.get(k), byteCodesMap.get(k));
        });
        ClassDefinition[] definitions = new ClassDefinition[reloadMap.size()];
        String[] classNames = new String[reloadMap.size()];
        int i = 0;
        for (Map.Entry<Class<?>, byte[]> entry : reloadMap.entrySet()) {
            classNames[i] = entry.getKey().getName();
            definitions[i++] = new ClassDefinition(entry.getKey(), entry.getValue());
        }
        try {
            logger.reload("Reloading classes {}", Arrays.toString(classNames));
            synchronized (hotswapLock) {
                Instrumentation instrumentation = DebugToolsBootstrap.INSTANCE.getInstrumentation();
                instrumentation.redefineClasses(definitions);
            }
            logger.reload("reloaded classes {}", Arrays.toString(classNames));
        } catch (Exception e) {
            logger.error("Fail to reload classes {}, msg is {}", Arrays.toString(classNames), e);
        }
    }
}
