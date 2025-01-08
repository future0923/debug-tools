# MyBatisPlus热重载 <Badge type="warning" text="beta" />

- Mybatis没试过，目前没有做Xml文件的更新识别，但是理论上应该不支持，但是可以尝试一下。
- SQL打印可以通过[打印执行SQL与耗时](sql.md)开启。

## Entity热重载

### 识别方式

- 类上含有 `com.baomidou.mybatisplus.annotation.TableName` 注解。
- 类或父类继承 `com.baomidou.mybatisplus.extension.activerecord.Model` 类。

### 热重载功能

类修改时会重新生成 Entity 类中的信息，如 `表字段` 、 `表名` 、`主键`等的映射关系，和重新启动的效果一样。**`当然也支持新增 Entity 类`**。

### 示例

#### 新增示例

单独了新增 Entity 没有实质意义，一般写代码的时候都会配合 [新增 Mapper](#add-mapper) 一起使用。

#### 修改示例

```java
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.ibatis.annotations.Mapper;

@TableName("user") // [!code --]
@TableName("dt_user") // [!code ++]
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long deptId;
    
    private String name; // [!code --]
    private String userName; // [!code ++]
    
    private Integer age; // [!code ++]
    
    private Integer sex; // [!code --]
}

@Mapper
public interface UserDao extends BaseMapper<User> {
    
    default List<User> getByDeptId(Long deptId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeptId, deptId);
        return selectList(wrapper);
    }
}
```

- 我们执行 `userDao.selectList()` 是之前会输出 `select id, deptId, name, sex from user`，重载之后会输出 `select id, deptId, user_name, age from dt_user`。

- 我们执行 `userDao.getByDeptId(1L)` 是之前会输出 `select id, deptId, name, sex from user where dept_id = 1`，重载之后会输出 `select id, deptId, user_name, age from dt_user where dept_id = 1`。

::: info 如果 Entity 对象被两个 Mapper 引用，那么两个 Mapper 执行时都会更新对应的 Entity 类信息

```java
@Mapper
public interface User1Dao extends BaseMapper<User> {

}

@Mapper
public interface User2Dao extends BaseMapper<User> {

}
```
:::

::: tip Entity信息的改变不会更新Mapper中的硬编码，如下面的 **getAll** 方法，不管 User 怎么更新，都会输出 **select id, deptId, name, sex from user**

```java
@Mapper
public interface UserDao extends BaseMapper<User> {
    
    @Select("select id, deptId, name, sex from user") // [!code focus]
    List<User> getAll(); // [!code focus]
}
```

::: 

## Mapper热重载

### 识别方式

- 必须是接口并且必须含有 `org.apache.ibatis.annotations.Mapper` 注解
- 类或父类继承 `com.baomidou.mybatisplus.core.mapper.BaseMapper` 接口。

### 热重载功能

Mapper 接口新增修改是会重新生成 Mapper 接口中的信息，包括 **默认方法**、**注解方法**等会重新生成代理类并注入到Spring Bean中，和重新启动的效果一样。**`当然也支持新增 Mapper 类`**。

### 示例

#### 新增示例 {#add-mapper}

**新增 Entity 文件**。

```java
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("user")
public class User {
    
    @TableId(type = IdType.AUTO)
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
public interface UserDao extends BaseMapper<User> {

    @Select("select user_name, count(1) from user group by user_name")
    Map<String, Integer> getUserCountByName();

    default List<User> getByUserName(String userName) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(User::getUserName, userName);
        return selectList(wrapper);
    }
}
```

新增 Service 文件使用 `UserDao` 接口。

```java
@Service
public class UserService {
    
    private final UserDao userDao;
    
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Map<String, Integer> getUserCountByName() {
        return userDao.getUserCountByName();
    }

    public List<User> getByUserName(String userName) {
        return userDao.getByUserName(userName);
    }
}
```

热重载之后，我们通过 [调用方法功能](attach-local.md) 调用 UserDao 和 UserService 新增的方法都可以正常执行。 

#### 修改示例

```java
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.ibatis.annotations.Mapper;

@TableName("user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long deptId;
    
    private String userName;
    
    private Integer age;
    
}

@Mapper
public interface UserDao extends BaseMapper<User> {

    @Select("select id, dept_id, name, sex from user") // [!code ++]
    List<User> getAll(); // [!code ++]
    
    default List<User> getByDeptId(Long deptId) { // [!code ++]
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>(); // [!code ++]
        wrapper.eq(User::getDeptId, deptId); // [!code ++]
        return selectList(wrapper); // [!code ++]
    } // [!code ++]
    
    @Select("select user_name, count(1) from user group by user_name") // [!code --]
    Map<String, Integer> getUserCountByName();  // [!code --]
    @Select("select sex, count(1) from user group by sex") // [!code ++]
    Map<String, Integer> getUserCountBySex();  // [!code ++]

    default List<User> getByUserName(String userName) { // [!code --]
    default List<User> getBySex(Integer sex) { // [!code ++]
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(User::getUserName, userName); // [!code --]
        wrapper.eq(User::getSex, sex); // [!code ++]
        return selectList(wrapper);
    }
    
}
```

- 可以执行新增的 `getAll()` 方法，输出 `select id, deptId, user_name, sex from user`。
- 可以执行新增的 `getByDeptId(1L)` 方法，输出 `select id, dept_id, user_name, age from user where dept_id = 1`。
- 之前的 `getUserCountByName()` 方法不存在了，可以执行修改后的 `getUserCountBySex()` 方法，输出 `select sex, count(1) from user group by sex`。
- 之前的 `getByUserName(String userName)` 方法不存在了，可以执行修改后的 `getBySex(Integer sex)` 方法，输出 `select id, dept_id, user_name, age from user where sex = xxx`。