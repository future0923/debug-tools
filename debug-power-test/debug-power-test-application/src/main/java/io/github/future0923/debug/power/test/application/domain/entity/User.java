package io.github.future0923.debug.power.test.application.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("dp_user")
@Data
public class User {

    @TableId
    private Integer id;

    private String name;

    private Integer age;

    private Integer version;
}
