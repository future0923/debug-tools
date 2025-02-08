package io.github.future0923.debug.tools.test.application.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author future0923
 */
@Data
@TableName(value = "dp_user")
public class DtUser {

    @TableId
    private Integer id;

    private String name;
}
