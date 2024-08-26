package io.github.future0923.debug.power.server.utils;

import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.dto.RunResultDTO;
import io.github.future0923.debug.power.common.enums.RunContentType;
import io.github.future0923.debug.power.common.utils.JdkUnsafeUtils;
import io.github.future0923.debug.power.server.http.DebugPowerHttpServer;
import io.github.future0923.debug.power.server.utils.dto.PageR;
import io.github.future0923.debug.power.server.utils.dto.ProfitBatchVO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author future0923
 */
class DebugPowerResultUtilsTest {

    @Test
    public void object() {
        RunDTO runDTO = new RunDTO();
        runDTO.setTargetClassName("io.github.future0923.debug.power.test.application.service.TestService");
        runDTO.setTargetMethodName("test");
        runDTO.setTargetMethodParameterTypes(Arrays.asList("java.lang.String", "java.lang.Integer"));
        Map<String, RunContentDTO> contentMap = new HashMap<>();
        contentMap.put("name", RunContentDTO.builder()
                .type(RunContentType.SIMPLE.getType())
                .content("future0923")
                .build());
        contentMap.put("age", RunContentDTO.builder()
                .type(RunContentType.SIMPLE.getType())
                .content("19")
                .build());
        runDTO.setTargetMethodContent(contentMap);

        //Properties runDTO = System.getProperties();
        RunResultDTO runResultDTO = new RunResultDTO(null, runDTO);
        Object valueByOffset = DebugPowerResultUtils.getValueByOffset(runDTO, runResultDTO.getFiledOffset());
        List<RunResultDTO> runResultDTOS = DebugPowerResultUtils.convertRunResultDTO(runDTO, runResultDTO.getFiledOffset());
        String filedOffset1 = runResultDTOS.get(4).getFiledOffset();
        String filedOffset2 = DebugPowerResultUtils.convertRunResultDTO(DebugPowerResultUtils.getValueByOffset(runDTO, filedOffset1), filedOffset1).get(0).getFiledOffset();
        List<RunResultDTO> runResultDTOS2 = DebugPowerResultUtils.convertRunResultDTO(DebugPowerResultUtils.getValueByOffset(runDTO, filedOffset2), filedOffset2);
        String filedOffset3 = runResultDTOS2.get(0).getFiledOffset();
        List<RunResultDTO> runResultDTOS1 = DebugPowerResultUtils.convertRunResultDTO(DebugPowerResultUtils.getValueByOffset(runDTO, filedOffset3), filedOffset3);
        Object valueByOffset1 = DebugPowerResultUtils.getValueByOffset(RunContentType.SIMPLE, "1");
        List<RunResultDTO> runResultDTOS3 = DebugPowerResultUtils.convertRunResultDTO(valueByOffset1, "1");
        System.out.println(runResultDTOS1);
    }

    @Test
    public void properties() {
        Properties runDTO = System.getProperties();
        RunResultDTO runResultDTO = new RunResultDTO(null, runDTO);
        DebugPowerResultUtils.convertRunResultDTO(runDTO, runResultDTO.getFiledOffset());
        System.out.println(runResultDTO);
    }

    @Test
    public void test() throws InterruptedException {
        DebugPowerHttpServer httpServer = DebugPowerHttpServer.getInstance();
        httpServer.start();

        RunDTO runDTO = new RunDTO();
        runDTO.setTargetClassName("io.github.future0923.debug.power.test.application.service.TestService");
        runDTO.setTargetMethodName("test");
        runDTO.setTargetMethodParameterTypes(Arrays.asList("java.lang.String", "java.lang.Integer"));
        Map<String, RunContentDTO> contentMap = new HashMap<>();
        contentMap.put("name", RunContentDTO.builder()
                .type(RunContentType.SIMPLE.getType())
                .content("future0923")
                .build());
        contentMap.put("age", RunContentDTO.builder()
                .type(RunContentType.SIMPLE.getType())
                .content("19")
                .build());
        runDTO.setTargetMethodContent(contentMap);

        RunResultDTO runResultDTO = new RunResultDTO(null, runDTO);
        DebugPowerResultUtils.putCache(runResultDTO.getFiledOffset(), runDTO);
        System.out.println(runResultDTO.getFiledOffset());

        Thread.sleep(1000000L);

    }

    @Test
    public void Integer() {
        Integer runDTO = 1;
        RunResultDTO runResultDTO = new RunResultDTO(null, runDTO);
        DebugPowerResultUtils.putCache(runResultDTO.getFiledOffset(), runDTO);
        System.out.println(runResultDTO.getFiledOffset());
        List<RunResultDTO> runResultDTOS = DebugPowerResultUtils.convertRunResultDTO(runDTO, runResultDTO.getFiledOffset());
        System.out.println(runResultDTOS);

    }

    @Test
    public void vo() {
        ProfitBatchVO vo1 = new ProfitBatchVO();
        vo1.setId(0L);
        vo1.setType(0);
        vo1.setSettlementBatchNo("");
        vo1.setStartTime(LocalDate.now());
        vo1.setEndTime(LocalDate.now());
        vo1.setPerformanceTime(LocalDateTime.now());
        vo1.setProfitTime(LocalDateTime.now());
        vo1.setCarryTime(LocalDateTime.now());
        vo1.setStatus(0);
        vo1.setStatusName("");
        vo1.setId(0L);
        vo1.setCreateBy(0L);
        vo1.setCreateByName("");
        vo1.setCreateTime(LocalDateTime.now());
        vo1.setUpdateBy(0L);
        vo1.setUpdateByName("");
        vo1.setUpdateTime(LocalDateTime.now());
        vo1.setVersion(0);
        vo1.setButPerm(new HashSet<Integer>());
        vo1.setButPcPerm(new HashSet<Integer>());
        PageR<ProfitBatchVO> runDTO = PageR.pageResp(1, 1, Arrays.asList(vo1));
        RunResultDTO runResultDTO = new RunResultDTO(null, runDTO);
        Object valueByOffset = DebugPowerResultUtils.getValueByOffset(runDTO, runResultDTO.getFiledOffset());
        List<RunResultDTO> runResultDTOS = DebugPowerResultUtils.convertRunResultDTO(runDTO, runResultDTO.getFiledOffset());
    }

}