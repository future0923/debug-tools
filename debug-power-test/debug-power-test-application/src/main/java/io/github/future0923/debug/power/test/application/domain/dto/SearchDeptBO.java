package io.github.future0923.debug.power.test.application.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 模块描述
 *
 * @author haibo.xin
 * @date 2023/9/26 11:01
 */
@Data
public class SearchDeptBO {

    /**
     * 是否存在下级
     * @mock
     */
    private Integer flagLevel;

    /**
     * 选择的部门id集合
     * @mock []
     */
    private List<Long> deptIds;
}