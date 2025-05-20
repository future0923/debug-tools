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