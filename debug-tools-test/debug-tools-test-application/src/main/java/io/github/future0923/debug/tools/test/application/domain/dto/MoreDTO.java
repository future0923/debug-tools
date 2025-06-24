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
package io.github.future0923.debug.tools.test.application.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author future0923
 */
@Data
public class MoreDTO extends TestDTO{

    private static final long serialVersionUID = 1L;

    /**
     * 交易编号
     */
    private String dealCode;

    /**
     * 交易状态（开启，结案，关闭）
     */
    private Integer dealStatus;

    /**
     * 交易类型(枚举，二手房交易)
     */
    private Integer dealType;

    /**
     * 成交金额（单位万元）
     */
    private BigDecimal dealAmount;

    /**
     * 付款方式 （枚举，一次性付款/按揭贷款/公积金贷款/混合贷款）
     */
    private Integer payMethod;

    /**
     * 是否是抵账房 1是 0否
     */
    private Integer isMortgageHouse;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 甲方经纪人id
     */
    private Long houseUserId;

    /**
     * 乙方经纪人id
     */
    private Long guestUserId;

    /**
     * 产权办理人id
     */
    private Long propertyRightUserId;

    /**
     * 贷款办理人id
     */
    private Long loansUserId;

    /**
     * 客源id
     */
    private Long guestId;

    /**
     * 客源编号
     */
    private String guestCode;

    /**
     * 客源需求id
     */
    private Long guestDetailId;

    /**
     * 客源需求编号
     */
    private String guestDetailCode;

    /**
     * 客源成交的客源需求
     */
    private String guestInfoDetail;

    /**
     * 客源数据快照
     */
    //@LogConvert(name = "客源数据快照")
    private String guestJson;

    /**
     * 客户姓名
     */
    private String guestName;

    /**
     * 客户身份证号
     */
    private String guestIdCard;

    /**
     * 客户代理人姓名
     */
    private String guestAgentName;

    /**
     * 客源电话集合
     */
    private String guestPhones;

    /**
     * 房源id
     */
    private Long houseId;

    /**
     * 房源编号
     */
    private String houseCode;

    /**
     * 房源数快照
     */
    //@LogConvert(name = "房源数快照")
    private String houseJson;

    /**
     * 业主姓名
     */
    private String ownerName;

    /**
     * 业主身份证号
     */
    private String ownerIdCard;

    /**
     * 业主代理人姓名
     */
    private String ownerAgentName;

    /**
     * 业主电话集合
     */
    private String ownerPhones;

    /**
     * 小区编码
     */
    private String codeVillage;

    /**
     * 小区名称
     */
    private String villageName;

    /**
     * 几室
     */
    private Integer numBedroom;

    /**
     * 几厅
     */
    private Integer numLivingRoom;

    /**
     * 几卫
     */
    private Integer numRestRoom;

    /**
     * 几厨
     */
    private Integer numKitchen;

    /**
     * 几阳台
     */
    private Integer numBalcony;

    /**
     * 建筑面积
     */
    private BigDecimal buildArea;

    /**
     * 行政区名称
     */
    private String regionName;

    /**
     * 片区名称
     */
    private String areaName;

    /**
     * 朝向
     */
    private Integer direction;

    /**
     * 封面路径
     */
    private String coverUrl;

    /**
     * 所在楼层
     */
    private Integer floor;

    /**
     * 总楼层
     */
    private Integer maxFloor;

    /**
     * 建造年代
     */
    private Integer buildYear;

    /**
     * 房屋用途
     */
    private Integer purpose;

    /**
     * 办件情况（id集合）
     */
    private String handleIds;

    /**
     * 办件情况（name集合)
     */
    private String handleNames;

    /**
     * 办件状态（枚举，未启动，已启动，无办件，已完成）DealHandleStatusEnum
     */
    private Integer handleStatus;

    /**
     * 办件进度，存配置项的code
     */
    private String handleCode;

    /**
     * 办件进度名称
     */
    private String handleName;

    /**
     * 办件节点，存配置项的code
     */
    private String handleNodeCode;

    /**
     * 办件进度名称
     */
    private String handleNodeName;

    /**
     * 收入证明模板ids集合
     */
    private String incomeIds;

    /**
     * 创建人id
     */
    private Long makerUserId;

    /**
     * 创建人姓名
     */
    private String makerUserName;

    /**
     * 创建部门id
     */
    private Long makerDeptId;

    /**
     * 创建部门名称
     */
    private String makerDeptName;

    /**
     * 交易创建时间
     */
    private LocalDateTime makerDate;

    /**
     * 贷款申请银行类型 枚举 DealBankTypeEnum
     */
    private Integer dealBankType;
    /**
     * 房源更换时间
     */
    private LocalDateTime replaceDate;

    /**
     * 是否已归档
     */
    private Integer flagArchive;

    /**
     * 一期主键id
     */
    private Integer hezId;

    /**
     * 更名时间
     */
    private Long renameTime;

    /**
     * 放贷时间
     */
    private Long lendingTime;

    /**
     * 住房宝ID
     */
    private Long zfbUserId;
}
