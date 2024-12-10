package io.github.future0923.debug.tools.server.scoket.handler;

import io.github.future0923.debug.tools.base.SpyAPI;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.HotSwapRequestPacket;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;

import java.io.OutputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author future0923
 */
public class HotSwapRequestHandler extends BasePacketHandler<HotSwapRequestPacket> {

    private static final Logger logger = Logger.getLogger(HotSwapRequestHandler.class);

    public static final HotSwapRequestHandler INSTANCE = new HotSwapRequestHandler();

    /**
     * instrumentation的redefineClasses锁
     */
    private final Object hotswapLock = new Object();

    private HotSwapRequestHandler() {

    }

    @Override
    public void handle(OutputStream outputStream, HotSwapRequestPacket packet) throws Exception {
        Map<Class<?>, byte[]> reloadMap = new HashMap<>();
        packet.getFilePathByteCodeMap().forEach((k, v) -> {
            try {
                reloadMap.put(SpyAPI.class.getClassLoader().loadClass(k), v);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
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
