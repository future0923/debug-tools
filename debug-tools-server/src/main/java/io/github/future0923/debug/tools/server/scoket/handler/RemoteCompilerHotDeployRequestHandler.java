package io.github.future0923.debug.tools.server.scoket.handler;

import io.github.future0923.debug.tools.base.classloader.DefaultClassLoader;
import io.github.future0923.debug.tools.common.protocal.packet.request.RemoteCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.server.compiler.DynamicCompiler;

import java.util.Map;

/**
 * @author future0923
 */
public class RemoteCompilerHotDeployRequestHandler extends AbstractHotDeployRequestHandler<RemoteCompilerHotDeployRequestPacket> {

    public static final RemoteCompilerHotDeployRequestHandler INSTANCE = new RemoteCompilerHotDeployRequestHandler();

    private RemoteCompilerHotDeployRequestHandler() {

    }

    @Override
    protected Map<String, byte[]> getByteCodes(RemoteCompilerHotDeployRequestPacket packet) {
        ClassLoader defaultClassLoader = DefaultClassLoader.getDefaultClassLoader();
        DynamicCompiler compiler = new DynamicCompiler(defaultClassLoader);
        packet.getFilePathByteCodeMap().forEach(compiler::addSource);
        return compiler.buildByteCodes();
    }
}
