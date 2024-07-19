package io.github.future0923.debug.power.idea.client.handler;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import io.github.future0923.debug.power.common.handler.BasePacketHandler;
import io.github.future0923.debug.power.common.protocal.packet.response.RunTargetMethodResponsePacket;

import java.io.OutputStream;

/**
 * @author future0923
 */
public class RunTargetMethodResponseHandler extends BasePacketHandler<RunTargetMethodResponsePacket> {

    public static final RunTargetMethodResponseHandler INSTANCE = new RunTargetMethodResponseHandler();

    private RunTargetMethodResponseHandler() {
    }

    @Override
    public void handle(OutputStream outputStream, RunTargetMethodResponsePacket packet) throws Exception {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Messages.showInfoMessage("返回值：" + packet.getPrintResult() + "\n异常信息：" + packet.getThrowable(), "运行结果：");
            }
        });

    }
}
