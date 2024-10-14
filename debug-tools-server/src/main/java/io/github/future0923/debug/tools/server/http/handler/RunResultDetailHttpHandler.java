package io.github.future0923.debug.tools.server.http.handler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.protocal.http.RunResultDetailReq;
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;

import java.util.List;

/**
 * @author future0923
 */
public class RunResultDetailHttpHandler extends BaseHttpHandler<RunResultDetailReq, List<RunResultDTO>> {

    public static final RunResultDetailHttpHandler INSTANCE = new RunResultDetailHttpHandler();

    public static final String PATH = "/result/detail";

    private RunResultDetailHttpHandler() {

    }
    @Override
    protected List<RunResultDTO> doHandle(RunResultDetailReq req, Headers responseHeaders) {
        String offsetPath = req.getOffsetPath();
        Object valueByOffset = DebugToolsResultUtils.getValueByOffset(offsetPath);
        return DebugToolsResultUtils.convertRunResultDTO(valueByOffset, offsetPath);
    }
}
