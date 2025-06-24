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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 模块
 * 模块描述
 *
 * @Author haibo.xin
 * @Date 2022-01-04 10:08
 */
@Data
public class PageR<T> implements Serializable {

    private static final long serialVersionUID = 12991202723179096L;

    /**
     * 返回标识
     * @mock 200
     */
    private int code;

    /**
     * 返回消息标识
     * @mock 操作成功
     */
    private String message;

    /**
     * 返回数据集
     * @mock arrayList
     */
    private List<T> data;

    /**
     * 加密字符串(与data 只能有一个有值)
     * @mock adfasdfasfdasdfasdf
     */
    private String encryptStr;

    /**
     * 分页总条数
     * @mock 10
     */
    private long total;

    private long subTotal;

    public PageR() {
    }

    public PageR(long total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    public PageR(long total, long subTotal, List<T> data) {
        this.total = total;
        this.subTotal = subTotal;
        this.data = data;
    }

    public static <T> PageR<T> empty() {
        return pageResp(0, Collections.emptyList());
    }

    public static <T> PageR<T> pageResp(long total, List<T> data) {
        return new PageR<>(total, data);
    }

    public static <T> PageR<T> pageResp(long total, long subTotal, List<T> data) {
        return new PageR<>(total,subTotal, data);
    }
}