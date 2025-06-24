/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
