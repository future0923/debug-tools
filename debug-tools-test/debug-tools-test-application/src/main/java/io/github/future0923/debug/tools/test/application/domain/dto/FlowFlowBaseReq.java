package io.github.future0923.debug.tools.test.application.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 审批模块查询基类
 *
 * @author haibo.xin
 * @date 2022/10/28 18:09
 */
@Data
public class FlowFlowBaseReq {
    /**
     * 每页显示条数
     *
     * @mock 10
     */
    protected Long size;

    /**
     * 当前页
     *
     * @mock 1
     */
    protected Long current;

    /**
     * 审核状态 枚举 FlowStatusEnum
     *
     * @mock 0
     */
    private Integer checkStatus;

    /**
     * 审核人
     *
     * @mock 123123
     */
    private Long checkUserId;

    /**
     * 审核人部门
     *
     * @mock 123123
     */
    private Long checkDeptId;

    /**
     * 审核人部门
     *
     * @mock 123123
     */
    private List<Long> checkDeptIds;

    /**
     * 开始时间(申请) 默认当前日期前三个月
     *
     * @mock 2022-12-12
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
    private LocalDate startDate;

    /**
     * 结束时间(申请) 默认当前时间
     *
     * @mock 2023-03-12
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
    private LocalDate endDate;

    /**
     * 审核日期
     *
     * @mock 2022-12-12
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
    private LocalDate startCheckDate;

    /**
     * 审核日期
     *
     * @mock 2023-03-12
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
    private LocalDate endCheckDate;

    /**
     * 审批项目小类
     *
     * @mock 1
     * @ignore
     */
    private Integer configItemChildType;

    /**
     * 业务数据id集合
     *
     * @mock 1
     * @ignore
     */
    private List<Long> businessIds;

    /**
     * 部门查询
     *
     * @mock
     */
    private SearchDeptBO searchDeptIds;
}