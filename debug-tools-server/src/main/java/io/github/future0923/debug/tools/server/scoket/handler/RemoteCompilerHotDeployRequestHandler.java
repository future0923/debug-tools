package io.github.future0923.debug.tools.server.scoket.handler;

import io.github.future0923.debug.tools.base.exception.DefaultClassLoaderException;
import io.github.future0923.debug.tools.common.protocal.packet.request.RemoteCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.server.compiler.DynamicCompiler;
import io.github.future0923.debug.tools.server.http.handler.AllClassLoaderHttpHandler;

import java.util.Map;

/**
 * @author future0923
 */
public class RemoteCompilerHotDeployRequestHandler extends AbstractHotDeployRequestHandler<RemoteCompilerHotDeployRequestPacket> {

    public static final RemoteCompilerHotDeployRequestHandler INSTANCE = new RemoteCompilerHotDeployRequestHandler();

    private RemoteCompilerHotDeployRequestHandler() {

    }

    @Override
    protected Map<String, byte[]> getByteCodes(RemoteCompilerHotDeployRequestPacket packet) throws DefaultClassLoaderException {
        DynamicCompiler compiler = new DynamicCompiler(getClassLoader(packet));
        packet.getFilePathByteCodeMap().forEach(compiler::addSource);
        return compiler.buildByteCodes();
    }

    @Override
    protected ClassLoader getClassLoader(RemoteCompilerHotDeployRequestPacket packet) throws DefaultClassLoaderException {
        return AllClassLoaderHttpHandler.getClassLoader(packet.getIdentity());
    }
}
