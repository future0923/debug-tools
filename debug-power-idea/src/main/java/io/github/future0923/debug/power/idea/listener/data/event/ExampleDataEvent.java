package io.github.future0923.debug.power.idea.listener.data.event;

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

    private Type type;

    public enum Type {
        SIMPLE,
        WITH_DEFAULT,
    }
}
