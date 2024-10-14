package io.github.future0923.debug.tools.test.application.dto;

import lombok.Data;

/**
 * @author future0923
 */
@Data
public class RecursionDTO {

    private String name;

    private RecursionDTO recursionDTO;
}
