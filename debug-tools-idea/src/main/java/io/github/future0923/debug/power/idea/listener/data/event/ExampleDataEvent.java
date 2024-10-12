package io.github.future0923.debug.power.idea.listener.data.event;

import io.github.future0923.debug.power.idea.setting.GenParamType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author future0923
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExampleDataEvent extends DataEvent{

    private GenParamType type;

}
