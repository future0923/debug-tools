package io.github.future0923.debug.power.test.application.domain.dto;

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
