package io.github.future0923.debug.power.server.http.headler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.power.common.dto.RunResultDTO;
import io.github.future0923.debug.power.common.protocal.http.RunResultDetailReq;
import io.github.future0923.debug.power.server.utils.DebugPowerResultUtils;

import java.util.List;

/**
 * @author future0923
 */
public class RunResultDetailHttpHandler extends BaseHttpHandler<RunResultDetailReq, List<RunResultDTO>> {

    public static final RunResultDetailHttpHandler INSTANCE = new RunResultDetailHttpHandler();

    private RunResultDetailHttpHandler() {

    }
    @Override
    protected List<RunResultDTO> doHandle(RunResultDetailReq req, Headers responseHeaders) {
        String offsetPath = req.getOffsetPath();
        Object valueByOffset = DebugPowerResultUtils.getValueByOffset(offsetPath);
        return DebugPowerResultUtils.convertRunResultDTO(valueByOffset, offsetPath);
    }
}
