# MyBatis热重载 <Badge type="warning" text="beta" />

- 如果使用MyBatisPlus请[点击](hot-reload-mybatis-plus.md)点击查看。
- SQL打印可以通过[打印执行SQL与耗时](sql.md)开启。
- MyBatis目前支持在 `Spring` 环境下，其他情况未知。

## Mapper热重载

### 识别方式

读取的是 `@MapperScan` 注解的包路径下的所有文件。

- 必须是接口并且必须含有 `org.apache.ibatis.annotations.Mapper` 注解
- 类或父类继承 `com.baomidou.mybatisplus.core.mapper.BaseMapper` 接口。

### 热重载功能

Mapper 接口新增修改是会重新生成 Mapper 接口中的信息，如 **注解方法**等会重新生成代理类并注入到Spring Bean中，和重新启动的效果一样。**`当然也支持新增 Mapper 类`**。

### 示例

#### 新增示例 {#add-mapper}

**新增 Entity 文件**。

```java
public class User {
    
    private Long id;

    private Long deptId;
    
    private String userName;

    private Integer age;
}
```

**新增 Mapper 文件**。

```java
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    @Select("select user_name, count(1) from user group by user_name")
    Map<String, Integer> getUserCountByName();

    List<User> selectByNameAndAge(@Param("name") String name, @Param("age") Integer age);
}
```

**新增 Xml 文件**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="UserMapper">
    <select id="selectByNameAndAge" resultType="User">
        select * from dp_user where name = #{name} and age = #{age}
    </select>
</mapper>
```

**新增 Service 文件使用 `UserMapper` 接口**。

```java
@Service
public class UserService {
    
    private final UserMapper userMapper;
    
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Map<String, Integer> getUserCountByName() {
        return userMapper.getUserCountByName();
    }
    
    public List<User> selectByNameAndAge(String name, Integer age) {
        return userMapper.selectByNameAndAge(name, age);
    }
}
```

热重载之后，我们通过 [调用方法功能](attach-local.md) 调用 UserMapper 和 UserService 新增的方法都可以正常执行。

#### 修改示例 {#modify-mapper}

```java
import org.apache.ibatis.annotations.Mapper;

public class User {
    
    private Long id;
    
    private Long deptId;
    
    private String userName;
    
    private Integer age;
    
}

@Mapper
public interface UserMapper {

    @Select("select id, dept_id, name, sex from user") // [!code ++]
    List<User> getAll(); // [!code ++]
    
    @Select("select user_name, count(1) from user group by user_name") // [!code --]
    Map<String, Integer> getUserCountByName();  // [!code --]
    @Select("select sex, count(1) from user group by sex") // [!code ++]
    Map<String, Integer> getUserCountBySex();  // [!code ++]

    List<User> selectByNameAndAge(@Param("name") String name, @Param("age") Integer age); // [!code ++]

    Integer selectCount(@Param("name") String name); // [!code --]
    Long selectCount(@Param("name") String name, @Param("age") Integer age); // [!code ++]
}
```

修改 `xml` 文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="UserMapper">
    <select id="selectByNameAndAge" resultType="User"> // [!code ++]
        select * from dp_user where name = #{name} and age = #{age} // [!code ++]
    </select> // [!code ++]

    <select id="selectCount" resultType="java.lang.Integer"> // [!code --]
        select * from dp_user where name = #{name} // [!code --]
    <select id="selectCount" resultType="java.lang.Long"> // [!code ++]
        select * from dp_user where name = #{name} and age = #{age} // [!code ++]
    </select>
</mapper>
```

热重载之后，我们通过 [调用方法功能](attach-local.md) 调用 UserMapper 修改后的方法都可以正常执行。

## Xml热重载

### 识别方式

获取xml资源文件变动（新增/修改）
- 新增：通过MyBatis验证的xml文件
- 修改：Configuration中loadedResources存在的xml资源

### 热重载功能

重新编译xml文件，并重新加载到Configuration中，让xml生效。

### 示例

#### 新增示例 {#add-xml}

一般需要配合mapper使用，例子查看[新增mapper](#add-mapper)。

#### 修改示例 {#modify-xml}

一般需要配合mapper使用，例子查看[修改mapper](#modify-mapper)，单独修改xml中的内容可以正常执行。