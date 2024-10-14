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
