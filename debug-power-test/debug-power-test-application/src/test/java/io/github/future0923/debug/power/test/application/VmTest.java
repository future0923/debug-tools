package io.github.future0923.debug.power.test.application;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.enums.RunContentType;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
public class VmTest {
    private static VirtualMachine vm;

    private static final String jarPath = "/Users/weilai/Documents/future0923/debug-power/debug-power-attach/target/debug-power-agent-jar-with-dependencies.jar";

    private String args;

    @BeforeAll
    public static void attach() throws Exception {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor virtualMachineDescriptor : list) {
            boolean b = virtualMachineDescriptor.displayName().contains("DebugPowerTestApplication");
            if (b) {
                System.out.println(virtualMachineDescriptor.displayName() + "\t" + virtualMachineDescriptor.id());
                vm = VirtualMachine.attach(virtualMachineDescriptor.id());
                break;
            }
        }
    }

    @AfterEach
    public void loadAgent() throws Exception {
        vm.loadAgent(jarPath, args);
    }

    @Test
    public void buildJsonStr() {
        args = "{\"targetClassName\":\"io.github.future0923.debug.power.test.application.service.TestService\",\"targetMethodName\":\"test\",\"targetMethodParameterTypes\":[\"java.lang.String\",\"java.lang.Integer\",\"io.github.future0923.debug.power.test.application.domain.TestEnum\",\"io.github.future0923.debug.power.test.application.service.Test1Service\",\"java.util.function.BiFunction\",\"io.github.future0923.debug.power.test.application.domain.dto.TestDTO\"],\"targetMethodContent\":{\"function\":{\"type\":\"lambda\",\"content\":\"(x, y) -> x + y\"},\"name\":{\"type\":\"simple\",\"content\":\"future0923\"},\"testEnum\":{\"type\":\"enum\",\"content\":\"TEST1\"},\"test1Service\":{\"type\":\"bean\"},\"age\":{\"type\":\"simple\",\"content\":\"19\"},\"dto\":{\"type\":\"json_entity\",\"content\":{\"name\":\"future0923\",\"age\":18}}}}";
    }

    @Test
    public void buildSimple() {
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
        args = DebugPowerJsonUtils.toJsonStr(runDTO);
    }

    @Test
    public void  buildLambda() {
        RunDTO runDTO = new RunDTO();
        runDTO.setTargetClassName("io.github.future0923.debug.power.test.application.service.TestService");
        runDTO.setTargetMethodName("test");
        runDTO.setTargetMethodParameterTypes(Arrays.asList("java.util.function.BiFunction"));
        Map<String, RunContentDTO> contentMap = new HashMap<>();
        contentMap.put("function", RunContentDTO.builder()
                .type(RunContentType.LAMBDA.getType())
                .content("(x, y) -> x + y")
                .build());
        runDTO.setTargetMethodContent(contentMap);
        args = DebugPowerJsonUtils.toJsonStr(runDTO);
    }

    @Test
    public void buildSpringBean() {
        RunDTO runDTO = new RunDTO();
        runDTO.setTargetClassName("io.github.future0923.debug.power.test.application.service.TestService");
        runDTO.setTargetMethodName("test");
        runDTO.setTargetMethodParameterTypes(Arrays.asList("io.github.future0923.debug.power.test.application.service.Test1Service"));
        Map<String, RunContentDTO> contentMap = new HashMap<>();
        contentMap.put("test1Service", RunContentDTO.builder()
                .type(RunContentType.BEAN.getType())
                .build());
        runDTO.setTargetMethodContent(contentMap);
        args = DebugPowerJsonUtils.toJsonStr(runDTO);
    }

    @Test
    public void  buildEntity() {
        RunDTO runDTO = new RunDTO();
        runDTO.setTargetClassName("io.github.future0923.debug.power.test.application.service.TestService");
        runDTO.setTargetMethodName("test");
        runDTO.setTargetMethodParameterTypes(Arrays.asList("io.github.future0923.debug.power.test.application.domain.dto.TestDTO"));
        Map<String, RunContentDTO> contentMap = new HashMap<>();
        contentMap.put("dto", RunContentDTO.builder()
                .type(RunContentType.JSON_ENTITY.getType())
                .content("{\"name\":\"future0923\",\"age\":18}")
                .build());
        runDTO.setTargetMethodContent(contentMap);
        args = DebugPowerJsonUtils.toJsonStr(runDTO);
    }

    @Test
    public void  buildAll() {
        RunDTO runDTO = new RunDTO();
        runDTO.setTargetClassName("io.github.future0923.debug.power.test.application.service.TestService");
        runDTO.setTargetMethodName("test");
        runDTO.setTargetMethodParameterTypes(Arrays.asList(
                "java.lang.String",
                "java.lang.Integer",
                "io.github.future0923.debug.power.test.application.service.Test1Service",
                "java.util.function.BiFunction",
                "io.github.future0923.debug.power.test.application.domain.dto.TestDTO"));
        Map<String, RunContentDTO> contentMap = new HashMap<>();
        contentMap.put("name", RunContentDTO.builder()
                .type(RunContentType.SIMPLE.getType())
                .content("future0923")
                .build());
        contentMap.put("age", RunContentDTO.builder()
                .type(RunContentType.SIMPLE.getType())
                .content("19")
                .build());
        contentMap.put("test1Service", RunContentDTO.builder()
                .type(RunContentType.BEAN.getType())
                .build());
        contentMap.put("function", RunContentDTO.builder()
                .type(RunContentType.LAMBDA.getType())
                .content("(x, y) -> x + y")
                .build());
        contentMap.put("dto", RunContentDTO.builder()
                .type(RunContentType.JSON_ENTITY.getType())
                .content("{\"name\":\"future0923\",\"age\":18}")
                .build());
        runDTO.setTargetMethodContent(contentMap);
        args = DebugPowerJsonUtils.toJsonStr(runDTO);
    }

    @Test
    public void buildBusiness() {
        RunDTO runDTO = new RunDTO();
        runDTO.setTargetClassName("com.estate.biz.business.module.deal.bank.DealBankService");
        runDTO.setTargetMethodName("listBankSchedule");
        runDTO.setTargetMethodParameterTypes(Arrays.asList("java.lang.Long"));
        Map<String, RunContentDTO> contentMap = new HashMap<>();
        contentMap.put("dealId", RunContentDTO.builder()
                .type(RunContentType.SIMPLE.getType())
                .content("0")
                .build());
        runDTO.setTargetMethodContent(contentMap);
        args = DebugPowerJsonUtils.toJsonStr(runDTO);
    }

    @Test
    public void buildFile() {
        args = "file://%2FUsers%2Fweilai%2FDocuments%2F/future0923%2Fdebug-power%2Fdebug-power-test%2Fdebug-power-test-application%2F.idea%2FDebugPower%2Fagent.json";
    }

    @Test
    public void buildList() {
        args = "{\n" +
                "    \"targetClassName\": \"io.github.future0923.debug.power.test.application.service.TestService\",\n" +
                "    \"targetMethodName\": \"test\",\n" +
                "    \"targetMethodParameterTypes\": [\n" +
                "        \"java.util.Collection\"\n" +
                "    ],\n" +
                "    \"targetMethodContent\": {\n" +
                "        \"obj\": {\n" +
                "            \"type\": \"json_entity\",\n" +
                "            \"content\": [\n" +
                "                1,\n" +
                "                2\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    @Test
    public void buildMap() {
        args = "{\n" +
                "    \"targetClassName\": \"io.github.future0923.debug.power.test.application.service.TestService\",\n" +
                "    \"targetMethodName\": \"test\",\n" +
                "    \"targetMethodParameterTypes\": [\n" +
                "        \"java.util.Map\"\n" +
                "    ],\n" +
                "    \"targetMethodContent\": {\n" +
                "       \"map\": {\n" +
                "           \"type\": \"json_entity\",\n" +
                "           \"content\": {\n" +
                "               \"1\": 2,\n" +
                "               \"3\": 4\n" +
                "           }\n" +
                "       }\n" +
                "   } \n" +
                "}";
    }

    @Test
    public void buildDaoSelect() {
        args = "{\n" +
                "    \"targetClassName\": \"io.github.future0923.debug.power.test.application.dao.UserDao\",\n" +
                "    \"targetMethodName\": \"selectByNameAndAge\",\n" +
                "    \"targetMethodParameterTypes\": [\n" +
                "        \"java.lang.String\",\n" +
                "        \"java.lang.Integer\"\n" +
                "    ],\n" +
                "    \"targetMethodContent\": {\n" +
                "        \"name\": {\n" +
                "            \"type\": \"simple\",\n" +
                "            \"content\": \"weilai\"\n" +
                "        },\n" +
                "        \"age\": {\n" +
                "            \"type\": \"simple\",\n" +
                "            \"content\": 18\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }
}
