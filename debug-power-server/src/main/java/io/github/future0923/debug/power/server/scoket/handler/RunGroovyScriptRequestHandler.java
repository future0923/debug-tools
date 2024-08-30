package io.github.future0923.debug.power.server.scoket.handler;

import groovy.lang.GroovyShell;
import io.github.future0923.debug.power.common.dto.RunResultDTO;
import io.github.future0923.debug.power.common.handler.BasePacketHandler;
import io.github.future0923.debug.power.common.protocal.packet.request.RunGroovyScriptRequestPacket;
import io.github.future0923.debug.power.common.protocal.packet.response.RunGroovyScriptResponsePacket;
import io.github.future0923.debug.power.server.DebugPowerBootstrap;
import io.github.future0923.debug.power.server.groovy.DebugPowerGroovyScript;
import io.github.future0923.debug.power.server.utils.DebugPowerResultUtils;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.OutputStream;

/**
 * @author future0923
 */
public class RunGroovyScriptRequestHandler extends BasePacketHandler<RunGroovyScriptRequestPacket> {

    public static final RunGroovyScriptRequestHandler INSTANCE = new RunGroovyScriptRequestHandler();

    private final GroovyShell groovyShell;

    private RunGroovyScriptRequestHandler() {
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.setScriptBaseClass(DebugPowerGroovyScript.class.getName());
        groovyShell = new GroovyShell(configuration);
    }

    @Override
    public void handle(OutputStream outputStream, RunGroovyScriptRequestPacket packet) throws Exception {
        String applicationName = DebugPowerBootstrap.serverConfig.getApplicationName();
        Object evaluateResult;
        try {
            evaluateResult = groovyShell.evaluate(packet.getScript());
        } catch (Exception e) {
            String offsetPath = RunResultDTO.genOffsetPath(e);
            DebugPowerResultUtils.putCache(offsetPath, e);
            writeAndFlushNotException(outputStream, RunGroovyScriptResponsePacket.of(e, offsetPath, applicationName));
            return;
        }
        RunGroovyScriptResponsePacket responsePacket = new RunGroovyScriptResponsePacket();
        responsePacket.setApplicationName(applicationName);
        responsePacket.setPrintResult(evaluateResult.toString());
        String offsetPath = RunResultDTO.genOffsetPath(evaluateResult);
        DebugPowerResultUtils.putCache(offsetPath, evaluateResult);
        responsePacket.setOffsetPath(offsetPath);
        writeAndFlushNotException(outputStream, responsePacket);
    }
}
