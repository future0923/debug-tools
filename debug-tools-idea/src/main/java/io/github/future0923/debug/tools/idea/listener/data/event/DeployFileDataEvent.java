package io.github.future0923.debug.tools.idea.listener.data.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author future0923
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeployFileDataEvent extends DataEvent {

    private DeployFileType fileType;

    public enum DeployFileType {
        Add,
        Delete,
        Clear,
        Reset,
    }

    public static DeployFileDataEvent ofAdd() {
        return new DeployFileDataEvent(DeployFileType.Add);
    }

    public static DeployFileDataEvent ofDelete() {
        return new DeployFileDataEvent(DeployFileType.Delete);
    }

    public static DeployFileDataEvent ofClear() {
        return new DeployFileDataEvent(DeployFileType.Clear);
    }

    public static DeployFileDataEvent ofReset() {
        return new DeployFileDataEvent(DeployFileType.Reset);
    }

}
