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
package io.github.future0923.debug.tools.test.spring.boot.mybatisplus.enums;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @FileName PaymentStatusEnum
 * @Description
 * @Author 22161
 * @date 2026-01-13
 **/
public enum PaymentStatusEnum {
    UNPAID(1, "未支付"),
    PAYING(2, "支付中"),
    PAID(3, "已支付"),
    FAILED(4, "支付失败"),
    REFUNDING(5, "退款中"),
    REFUNDED(6, "已退款"),
    CLOSED(7, "已关闭");

    private final Integer value;
    private final String desc;

    // 可支付的状态集合
    private static final List<Integer> CAN_PAY_STATUS_SET = List.of(
            UNPAID.getValue(),
            FAILED.getValue()
    );

    // 可退款的状态集合
    private static final List<Integer> CAN_REFUND_STATUS_SET = List.of(
            PAID.getValue()
    );

    // 可关闭的状态列表
    public static final List<PaymentStatusEnum> CAN_CLOSE_STATUS_LIST = Arrays.asList(
            UNPAID,
            FAILED
    );

    // 可重试的状态列表
    public static final List<PaymentStatusEnum> CAN_RETRY_STATUS_LIST = Arrays.asList(
            FAILED
    );

    /**
     * 判断状态是否可支付
     */
    public static boolean canPay(Integer status) {
        return CAN_PAY_STATUS_SET.contains(status);
    }

    /**
     * 判断状态是否可退款
     */
    public static boolean canRefund(Integer status) {
        return CAN_REFUND_STATUS_SET.contains(status);
    }

    /**
     * 判断状态是否可关闭
     */
    public static boolean canClose(Integer status) {
        return CAN_CLOSE_STATUS_LIST.stream()
                .map(PaymentStatusEnum::getValue)
                .anyMatch(value -> value.equals(status));
    }

    /**
     * 判断状态是否可重试支付
     */
    public static boolean canRetry(Integer status) {
        return CAN_RETRY_STATUS_LIST.stream()
                .map(PaymentStatusEnum::getValue)
                .anyMatch(value -> value.equals(status));
    }

    /**
     * 根据值获取枚举
     */
    public static PaymentStatusEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(e -> e.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据值获取描述
     */
    public static String getDescByValue(Integer value) {
        return Optional.ofNullable(getByValue(value))
                .map(PaymentStatusEnum::getDesc)
                .orElse(null);
    }

    /**
     * 获取所有支付成功相关的状态
     */
    public static Set<Integer> getSuccessRelatedStatusValues() {
        return Arrays.stream(values())
                .filter(e -> e == PAID || e == REFUNDED)
                .map(PaymentStatusEnum::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * 获取所有可操作的状态（支付失败后可重试等）
     */
    public static Set<Integer> getOperableStatusValues() {
        return Arrays.stream(values())
                .filter(e -> canRetry(e.getValue()) || canRefund(e.getValue()))
                .map(PaymentStatusEnum::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * 判断当前枚举值是否等于指定值
     */
    public boolean eq(Integer value) {
        return this.value.equals(value);
    }

    /**
     * 判断是否为终态（不再变化的状态）
     */
    public static boolean isFinalStatus(Integer status) {
        return Arrays.asList(REFUNDED.getValue(), CLOSED.getValue())
                .contains(status);
    }

    public Integer getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    private PaymentStatusEnum(final Integer value, final String desc) {
        this.value = value;
        this.desc = desc;
    }
}
