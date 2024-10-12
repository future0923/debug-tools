package io.github.future0923.debug.power.test.application.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 区间类检索
 *
 * @author haibo.xin
 * @date 2023/2/18 9:20
 */
@Data
public class ExtendSectionSearch<T> {

    /**
     * 区间标识
     *
     * @mock
     */
    String searchKey;

    /**
     * 区间最小值
     *
     * @mock
     */
    T min;

    /**
     * 区间最大值
     *
     * @mock
     */
    T max;

    /**
     * 用来接收枚举
     *
     * @mock
     */
    Object code;

    /**
     * 时间区间
     *
     * @mock [2023-12-12 12:12:12]
     */
    List<T> queryDate;
}