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
 * @FileName TaskStatusEnum
 * @Description
 * @Author 22161
 * @date 2026-01-13
 **/
public enum TaskStatusEnum {
    PENDING(1, "待处理"),
    PROCESSING(2, "处理中"),
    REVIEWING(3, "审核中"),
    COMPLETED(4, "已完成"),
    REJECTED(5, "已拒绝"),
    CANCELLED(6, "已取消");

    private final Integer value;
    private final String desc;

    // 可重新分配的状态集合
    private static final List<Integer> CAN_REASSIGN_STATUS_SET = List.of(
            PENDING.getValue(),
            REJECTED.getValue()
    );

    // 可修改的状态集合
    private static final List<Integer> CAN_MODIFY_STATUS_SET = List.of(
            PENDING.getValue(),
            PROCESSING.getValue()
    );

    // 可完成的状态列表
    public static final List<TaskStatusEnum> CAN_COMPLETE_STATUS_LIST = Arrays.asList(
            PROCESSING,
            REVIEWING
    );

    // 可取消的状态列表
    public static final List<TaskStatusEnum> CAN_CANCEL_STATUS_LIST = Arrays.asList(
            PENDING,
            PROCESSING
    );
    public static final List<PaymentStatusEnum> PAYMENT_RELATED_STATUS_LIST = Arrays.asList(
            PaymentStatusEnum.UNPAID,    // 等待支付
            PaymentStatusEnum.PAID,        // 已支付
            PaymentStatusEnum.REFUNDING    // 退款中
    );
    public static final List<PaymentStatusEnum> COMPLETABLE_PAYMENT_STATUS_LIST = Arrays.asList(
            PaymentStatusEnum.PAID,        // 已支付
            PaymentStatusEnum.REFUNDED     // 已退款
    );
    /**
     * 判断状态是否可重新分配
     */
    public static boolean canReassign(Integer status) {
        return CAN_REASSIGN_STATUS_SET.contains(status);
    }
    public static boolean isPaymentRelated(Integer paymentStatus) {
        if (paymentStatus == null) {
            return false;
        }
        return PAYMENT_RELATED_STATUS_LIST.stream()
                .map(PaymentStatusEnum::getValue)
                .anyMatch(value -> value.equals(paymentStatus));
    }
    public static boolean isPaymentCompletable(Integer paymentStatus) {
        if (paymentStatus == null) {
            return false;
        }
        return COMPLETABLE_PAYMENT_STATUS_LIST.stream()
                .map(PaymentStatusEnum::getValue)
                .anyMatch(value -> value.equals(paymentStatus));
    }
    /**
     * 判断状态是否可修改
     */
    public static boolean canModify(Integer status) {
        return CAN_MODIFY_STATUS_SET.contains(status);
    }

    /**
     * 判断状态是否可完成
     */
    public static boolean canComplete(Integer status) {
        return CAN_COMPLETE_STATUS_LIST.stream()
                .map(TaskStatusEnum::getValue)
                .anyMatch(value -> value.equals(status));
    }

    /**
     * 判断状态是否可取消
     */
    public static boolean canCancel(Integer status) {
        return CAN_CANCEL_STATUS_LIST.stream()
                .map(TaskStatusEnum::getValue)
                .anyMatch(value -> value.equals(status));
    }

    /**
     * 根据值获取枚举
     */
    public static TaskStatusEnum getByValue(Integer value) {
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
                .map(TaskStatusEnum::getDesc)
                .orElse(null);
    }

    /**
     * 判断当前枚举值是否等于指定值
     */
    public boolean eq(Integer value) {
        return this.value.equals(value);
    }

    /**
     * 获取所有可修改状态的值
     */
    public static Set<Integer> getModifiableStatusValues() {
        return CAN_MODIFY_STATUS_SET.stream()
                .collect(Collectors.toSet());
    }

    public Integer getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    private TaskStatusEnum(final Integer value, final String desc) {
        this.value = value;
        this.desc = desc;
    }
}
