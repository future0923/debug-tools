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
package io.github.future0923.debug.tools.test.application.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * 交接单审核表 查询条件
 *
 * @author MA.YONG.DUO
 * @date 2023-03-08 16:47:18
 */
@Getter
@Setter
public class DealFilesHandoverCheckReq extends FlowFlowBaseReq {
    private String appInfo;
    private String dealCode;
    private Integer dealType;
    private Long makerUserId;
    private Long makerDeptId;
    private List<Long> deptIds;
    private Integer makerDateCode;
    private List<LocalDate> makerDates;
    private List<LocalDate> checkDates;
    private ExtendSectionSearch<LocalDate> applyDate;
}
