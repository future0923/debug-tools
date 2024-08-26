package io.github.future0923.debug.power.server.http.headler;

import cn.hutool.json.JSONUtil;
import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.power.common.dto.RunResultDTO;
import io.github.future0923.debug.power.common.enums.PrintResultType;
import io.github.future0923.debug.power.common.protocal.http.RunResultTypeReq;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.server.utils.DebugPowerResultUtils;

/**
 * @author future0923
 */
public class RunResultTypeHttpHandler extends BaseHttpHandler<RunResultTypeReq, Object> {

    public static final RunResultTypeHttpHandler INSTANCE = new RunResultTypeHttpHandler();

    private RunResultTypeHttpHandler() {

    }

    @Override
    protected Object doHandle(RunResultTypeReq req, Headers responseHeaders) {
        String offsetPath = req.getOffsetPath();
        Object valueByOffset = DebugPowerResultUtils.getValueByOffset(offsetPath);
        if (valueByOffset == null) {
            return null;
        }
        if (PrintResultType.TOSTRING.getType().equals(req.getPrintResultType())) {
            return valueByOffset.toString();
        }
        if (PrintResultType.JSON.getType().equals(req.getPrintResultType())) {
            try {
                return DebugPowerJsonUtils.toJsonPrettyStr(valueByOffset);
            } catch (Exception e) {
                return "{\n\"result\": \"" + e.getMessage()+"\"\n}";
            }
        } else if (PrintResultType.DEBUG.getType().equals(req.getPrintResultType())) {
            return new RunResultDTO("result", valueByOffset);
        }
        return null;
    }
}
