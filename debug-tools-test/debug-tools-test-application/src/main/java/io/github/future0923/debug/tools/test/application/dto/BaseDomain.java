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

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * entity基类
 *
 * @author haibo.xin
 * @date 2022/3/20 9:23
 */
@Data
public class BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识id
     * @mock 1234567891011121
     */
    private Long id;

    /**
     * 创建人
     * @mock 创建人
     */
    private Long createBy;

    /**
     * 创建人
     *
     * @mock 创建人名称
     */
    private String createByName;

    /**
     * 创建时间
     *
     * @mock 2022-12-12 12:12:12
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;
    /**
     * 更新人
     * @mock 更新人
     */
    private Long updateBy;

    /**
     * 更新人
     *
     * @mock 更新人名称
     */
    private String updateByName;

    /**
     * 更新时间
     *
     * @mock 2022-12-12 12:12:12
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;
    /**
     * 版本 默认为0
     * @mock 0
     * @required
     */
    private Integer version;

    /**
     * 按钮权限(APP)
     * @mock [0]
     */
    private Set<Integer> butPerm;

    /**
     * 按钮权限(PC)
     * @mock [0]
     */
    private Set<Integer> butPcPerm;
}