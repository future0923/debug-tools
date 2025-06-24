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

import io.github.future0923.debug.tools.base.hutool.core.convert.Convert;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import groovy.lang.GroovyShell;
import io.github.future0923.debug.tools.base.classloader.GroovyScriptClassLoader;
import io.github.future0923.debug.tools.base.exception.DefaultClassLoaderException;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.ResultClassType;
import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunGroovyScriptRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunGroovyScriptResponsePacket;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.groovy.DebugToolsGroovyScript;
import io.github.future0923.debug.tools.server.http.handler.AllClassLoaderHttpHandler;
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.OutputStream;

/**
 * @author future0923
 */
public class RunGroovyScriptRequestHandler extends BasePacketHandler<RunGroovyScriptRequestPacket> {

    public static final RunGroovyScriptRequestHandler INSTANCE = new RunGroovyScriptRequestHandler();

    private RunGroovyScriptRequestHandler() {

    }

    @Override
    public void handle(OutputStream outputStream, RunGroovyScriptRequestPacket packet) throws Exception {
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.setScriptBaseClass(DebugToolsGroovyScript.class.getName());
        String applicationName = DebugToolsBootstrap.serverConfig.getApplicationName();
        GroovyScriptClassLoader groovyScriptClassLoader = GroovyScriptClassLoader.init(AllClassLoaderHttpHandler.getDebugToolsClassLoader());
        try {
            groovyScriptClassLoader.setDefaultClassLoader(AllClassLoaderHttpHandler.getClassLoader(packet.getIdentity()));
        } catch (DefaultClassLoaderException e) {
            String offsetPath = RunResultDTO.genOffsetPathRandom(e);
            DebugToolsResultUtils.putCache(offsetPath, e);
            writeAndFlushNotException(outputStream, RunGroovyScriptResponsePacket.of(e, offsetPath, applicationName));
            return;
        }
        GroovyShell groovyShell = new GroovyShell(groovyScriptClassLoader, configuration);
        Object evaluateResult;
        try {
            evaluateResult = groovyShell.evaluate(packet.getScript());
        } catch (Exception e) {
            String offsetPath = RunResultDTO.genOffsetPathRandom(e);
            DebugToolsResultUtils.putCache(offsetPath, e);
            writeAndFlushNotException(outputStream, RunGroovyScriptResponsePacket.of(e, offsetPath, applicationName));
            return;
        }
        RunGroovyScriptResponsePacket responsePacket = new RunGroovyScriptResponsePacket();
        responsePacket.setApplicationName(applicationName);
        if (evaluateResult == null) {
            responsePacket.setResultClassType(ResultClassType.NULL);
            responsePacket.setPrintResult("NULL");
        } else if (ClassUtil.isSimpleValueType(evaluateResult.getClass())) {
            responsePacket.setResultClassType(ResultClassType.SIMPLE);
            responsePacket.setPrintResult(Convert.toStr(evaluateResult));
        } else {
            responsePacket.setResultClassType(ResultClassType.OBJECT);
            responsePacket.setPrintResult(evaluateResult.toString());
            String offsetPath = RunResultDTO.genOffsetPathRandom(evaluateResult);
            responsePacket.setOffsetPath(offsetPath);
            DebugToolsResultUtils.putCache(offsetPath, evaluateResult);
        }
        writeAndFlushNotException(outputStream, responsePacket);
    }
}
