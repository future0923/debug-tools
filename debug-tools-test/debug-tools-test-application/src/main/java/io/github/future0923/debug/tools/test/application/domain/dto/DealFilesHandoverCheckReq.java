/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
