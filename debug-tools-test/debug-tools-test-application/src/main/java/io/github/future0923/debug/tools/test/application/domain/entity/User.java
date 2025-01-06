package io.github.future0923.debug.tools.test.application.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

@TableName("dp_user")
@Data
public class User extends Model<User> {

    @TableId
    private Integer id;

    private String name;

    private Integer age;

    private Integer version;
}
