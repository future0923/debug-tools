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
package io.github.future0923.debug.tools.test.application.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author future0923
 */
@Data
public class ProfitBatchVO extends BaseDomain {

    /**
     * 唯一标识
     * @mock 54685412575698542348
     */
    private Long id;


    /**
     * 类型（ProfitBatchTypeEnum）
     * @mock 11
     */
    private Integer type;

    /**
     * 结算批次号
     * @mock 测试数据
     */
    private String settlementBatchNo;

    /**
     * 利润开始时间
     * @mock 测试数据
     */
    private LocalDate startTime;

    /**
     * 业绩结束时间
     * @mock 测试数据
     */
    private LocalDate endTime;

    /**
     * 业绩计算时间
     * @mock 2022-02-02 12:12:12
     */
    private LocalDateTime performanceTime;

    /**
     * 利润计算时间
     * @mock 2022-02-02 12:12:12
     */
    private LocalDateTime profitTime;

    /**
     * 经纪人工资结转时间
     * @mock 2022-02-02 12:12:12
     */
    private LocalDateTime carryTime;

    /**
     * 状态（ProfitBatchStatusEnum）
     * @mock 11
     */
    private Integer status;

    /**
     * 状态（ProfitBatchStatusEnum）
     * @mock 11
     */
    private String statusName;
}
