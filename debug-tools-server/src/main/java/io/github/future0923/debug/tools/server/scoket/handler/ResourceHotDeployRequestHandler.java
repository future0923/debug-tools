/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.server.scoket.handler;

import io.github.future0923.debug.tools.base.exception.DefaultClassLoaderException;
import io.github.future0923.debug.tools.base.hutool.core.exceptions.ExceptionUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsOSUtils;
import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.ResourceHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.HotDeployResponsePacket;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.http.handler.AllClassLoaderHttpHandler;

import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

/**
 * @author future0923
 */
public class ResourceHotDeployRequestHandler extends BasePacketHandler<ResourceHotDeployRequestPacket> {

    private static final Logger logger = Logger.getLogger(ResourceHotDeployRequestHandler.class);

    public static final ResourceHotDeployRequestHandler INSTANCE = new ResourceHotDeployRequestHandler();

    private ResourceHotDeployRequestHandler() {

    }

    @Override
    public void handle(OutputStream outputStream, ResourceHotDeployRequestPacket packet) throws Exception {
        Map<String, byte[]> filePathByteCodeMap = packet.getFilePathByteCodeMap();
        String reloadClass = String.join(", ", filePathByteCodeMap.keySet());
        ClassLoader defaultClassLoader;
        try {
            defaultClassLoader = AllClassLoaderHttpHandler.getClassLoader(packet.getIdentity());
        } catch (DefaultClassLoaderException e) {
            logger.error("Fail to reload classes {}, msg is {}", reloadClass, e);
            writeAndFlushNotException(outputStream, HotDeployResponsePacket.of(false, "Hot deploy error, file [" + reloadClass + "]\n" + ExceptionUtil.stacktraceToString(e, -1), DebugToolsBootstrap.serverConfig.getApplicationName()));
            return;
        }
        writeFile(defaultClassLoader, filePathByteCodeMap);
    }

    protected void writeFile(ClassLoader defaultClassLoader, Map<String, byte[]> byteCodesMap) {
        PluginConfiguration pluginConfiguration = PluginManager.getInstance().getPluginConfiguration(defaultClassLoader);
        if (pluginConfiguration == null) {
            logger.error("Failure to retrieve PluginConfiguration. Please ensure that the project is started in hot reload mode.");
            return;
        }
        URL[] resourcesPath = pluginConfiguration.getWatchResources();
        if (resourcesPath == null || resourcesPath.length == 0) {
            logger.error("{} is null", DebugToolsOSUtils.isWindows() ? "watchResourcesWin" : "watchResources");
            return;
        }
        String watchResourcesPath = resourcesPath[0].getPath();
        if (!watchResourcesPath.endsWith(File.separator)) {
            watchResourcesPath += File.separator;
        }
        for (Map.Entry<String, byte[]> entry : byteCodesMap.entrySet()) {
            DebugToolsFileUtils.writeBytes(entry.getValue(), watchResourcesPath + entry.getKey());
        }
    }
}
