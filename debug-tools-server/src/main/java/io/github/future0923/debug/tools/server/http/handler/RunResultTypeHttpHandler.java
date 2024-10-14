package io.github.future0923.debug.tools.server.http.handler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.PrintResultType;
import io.github.future0923.debug.tools.common.protocal.http.RunResultTypeReq;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;

/**
 * @author future0923
 */
public class RunResultTypeHttpHandler extends BaseHttpHandler<RunResultTypeReq, Object> {

    public static final RunResultTypeHttpHandler INSTANCE = new RunResultTypeHttpHandler();

    public static final String PATH = "/result/type";

    private RunResultTypeHttpHandler() {

    }

    @Override
    protected Object doHandle(RunResultTypeReq req, Headers responseHeaders) {
        String offsetPath = req.getOffsetPath();
        Object valueByOffset = DebugToolsResultUtils.getValueByOffset(offsetPath);
        if (valueByOffset == null) {
            return null;
        }
        if (PrintResultType.TOSTRING.getType().equals(req.getPrintResultType())) {
            return valueByOffset.toString();
        }
        if (PrintResultType.JSON.getType().equals(req.getPrintResultType())) {
            try {
                return DebugToolsJsonUtils.toJsonPrettyStr(valueByOffset);
            } catch (Exception e) {
                return "{\n\"result\": \"" + e.getMessage()+"\"\n}";
            }
        } else if (PrintResultType.DEBUG.getType().equals(req.getPrintResultType())) {
            return new RunResultDTO("result", valueByOffset, RunResultDTO.Type.ROOT, offsetPath);
        }
        return null;
    }
}
