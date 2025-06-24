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
package io.github.future0923.debug.tools.server.utils.dto;

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
