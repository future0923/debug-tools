# MyBatis hot reload <Badge type="warning" text="beta" />

- If you use MyBatisPlus, please [click](hot-reload-mybatis-plus.md) to view.
- SQL printing can be enabled by [Printing SQL execution and time consumption](sql.md).
- MyBatis currently supports the `Spring` environment, and other situations are unknown.

## Mapper hot reload

### Identification method

All files under the package path of the `@MapperScan` annotation are read.

- It must be an interface and must contain the `org.apache.ibatis.annotations.Mapper` annotation
- The class or parent class inherits the `com.baomidou.mybatisplus.core.mapper.BaseMapper` interface.

::: warning

Because the bytecode injection is modified at startup, it currently does not support reading the variable content configured by `@MapperScan`. Please write the real package path.

The configuration that works is as follows:<br>
@MapperScan("io.github.future0923.test.dao")<br>
@MapperScan("io.github.future0923.test.\*\*.dao")<br>
@MapperScan("io.github.future0923.test.\*\*")<br>
@MapperScan({"io.github.future0923.test.user.\*\*.dao", "io.github.future0923.test.order.\*\*.dao"})<br>

The configuration that does not work is as follows:<br>
@MapperScan("${mybatis-plus.mapperPackage}")

:::

### Hot reload function

Adding new changes to the Mapper interface will regenerate the information in the Mapper interface, such as **annotation methods**, etc. will regenerate the proxy class and inject it into the Spring Bean, which has the same effect as restarting. **`Of course, it also supports adding new Mapper classes`**.

### Example

#### Add new example {#add-mapper}

**Add new Entity file**.

```java
public class User {
    
    private Long id;
    
    private Long deptId;
    
    private String userName;
    
    private Integer age;
}
```

**Add new Mapper file**.

```java
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    @Select("select user_name, count(1) from user group by user_name")
    Map<String, Integer> getUserCountByName();

    List<User> selectByNameAndAge(@Param("name") String name, @Param("age") Integer age);
}
```

**Add new Mapper file**.

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="UserMapper">
    <select id="selectByNameAndAge" resultType="User">
        select * from dp_user where name = #{name} and age = #{age}
    </select>
</mapper>
```

**Add a new Service file to use the `UserMapper` interface**.

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

After hot reloading, we can call the new methods of UserMapper and UserService through [Call Method Function](attach-local.md) and they can be executed normally.

#### Modify example {#modify-mapper}


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

After hot reloading, we can call the modified method of UserMapper through [Call Method Function](attach-local.md) and it can be executed normally.

## Xml hot reload

### Identification method

Get xml resource file changes (add/modify)
- Add: xml file verified by MyBatis
- Modify: xml resource existing in loadedResources in Configuration

### Hot reload function

Recompile xml file and reload it into Configuration to make xml effective.

### Example

#### Add example {#add-xml}

Generally need to be used with mapper, see [Add mapper](#add-mapper) for examples.

#### Modify example {#modify-xml}

Generally need to be used with mapper, see [Modify mapper](#modify-mapper) for examples, modifying the content in xml alone can be executed normally.