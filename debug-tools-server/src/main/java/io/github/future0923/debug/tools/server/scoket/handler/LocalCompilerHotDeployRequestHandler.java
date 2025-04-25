package io.github.future0923.debug.tools.server.scoket.handler;

import io.github.future0923.debug.tools.base.exception.DefaultClassLoaderException;
import io.github.future0923.debug.tools.common.protocal.packet.request.LocalCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.server.http.handler.AllClassLoaderHttpHandler;

import java.util.Map;

/**
 * @author future0923
 */
public class LocalCompilerHotDeployRequestHandler extends AbstractHotDeployRequestHandler<LocalCompilerHotDeployRequestPacket> {

    public static final LocalCompilerHotDeployRequestHandler INSTANCE = new LocalCompilerHotDeployRequestHandler();

    private LocalCompilerHotDeployRequestHandler() {

    }

    @Override
    protected Map<String, byte[]> getByteCodes(LocalCompilerHotDeployRequestPacket packet) {
        return packet.getFilePathByteCodeMap();
    }

    @Override
    protected ClassLoader getClassLoader(LocalCompilerHotDeployRequestPacket packet) throws DefaultClassLoaderException {
        return AllClassLoaderHttpHandler.getClassLoader(packet.getIdentity());
    }
}
