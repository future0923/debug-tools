package io.github.future0923.debug.power.common.dto;

import io.github.future0923.debug.power.common.enums.RunContentType;
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
public class RunContentDTO {

    /**
     * {@link RunContentType}
     */
    private String type;

    private Object content;

    private Object value;

}
