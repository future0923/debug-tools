package io.github.future0923.debug.power.common.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
@Data
public class RunDTO {

    private Map<String, String> headers;

    private String targetClassName;

    private String targetMethodName;

    private List<String> targetMethodParameterTypes;

    private Map<String, RunContentDTO> targetMethodContent;

    private String xxlJobParam;

}
