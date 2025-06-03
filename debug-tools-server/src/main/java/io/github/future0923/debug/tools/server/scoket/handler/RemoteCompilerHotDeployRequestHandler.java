/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
