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
package io.github.future0923.debug.tools.idea.model;

import io.github.future0923.debug.tools.base.hutool.core.date.LocalDateTimeUtil;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author future0923
 */
@Data
public class InvokeMethodRecordDTO {

    private String identity;

    private RunStatus runStatus = RunStatus.RUNNING;

    private Long duration;

    private String runTime;

    /**
     * 全类名
     */
    private String className;

    /**
     * 简类名
     */
    private String classSimpleName;

    /**
     * 方法名
     */
    private String methodName;

    private String methodSignature;

    /**
     * 方法参数
     */
    private String methodParamJson;

    private String methodAroundName;

    private String cacheKey;

    /**
     * 调用参数
     */
    private String runDTO;

    public RunDTO parseRunDTO() {
        return DebugToolsJsonUtils.toBean(runDTO, RunDTO.class);
    }

    public void formatRunDTO(RunDTO runDTO) {
        this.runDTO = DebugToolsJsonUtils.toJsonStr(runDTO);
    }

    public void formatRunTime() {
        setRunTime(LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm"));
    }
}
