package io.github.future0923.debug.power.test.application.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author future0923
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDTO {

    private String name;

    private Integer age;
}
