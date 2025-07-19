package io.github.future0923.debug.tools.common.dto;

import lombok.Data;

/**
 * @author future0923
 */
@Data
public class TraceMethodDTO {

    /**
     * 是否追踪方法耗时
     */
    private Boolean traceMethod = false;

    /**
     * 追踪方法最大深度
     */
    private Integer traceMaxDepth = 1;

    /**
     * 追踪MyBatis方法
     */
    private Boolean traceMyBatis = false;

    /**
     * 追踪SQL
     */
    private Boolean traceSQL = false;

    /**
     * 追踪方法忽略的包前缀
     */
    private String traceIgnorePackage;
}
