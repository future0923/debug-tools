# MyBatisPlus Hot Reload <Badge type="warning" text="beta" />

- I haven't tried Mybatis. Currently, there is no update recognition for Xml files. But in theory, it should not be supported, but you can try it.
- SQL printing can be enabled through [Printing Execution SQL and Time](sql.md).

## Entity Hot Reload

### Identification method

- The class contains the `com.baomidou.mybatisplus.annotation.TableName` annotation.
- The class or parent class inherits the `com.baomidou.mybatisplus.extension.activerecord.Model` class.

### Expected Results

When the class is modified, the information in the Entity class will be regenerated, such as the mapping relationship of `table fields`, `table name`, `primary key`, etc., which is the same as restarting. **`Of course, it also supports adding new Entity classes`**.

### Example

#### Add

Adding a new Entity alone does not make any sense. It is usually used together with [adding a Mapper](#add-mapper) when writing code.

#### Modify

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

- When we execute `userDao.selectList()`, it will output `select id, deptId, name, sex from user` before, and it will output `select id, deptId, user_name, age from dt_user` after reloading.
- When we execute `userDao.getByDeptId(1L)`, it will output `select id, deptId, name, sex from user where dept_id = 1` before, and it will output `select id, deptId, user_name, age from dt_user where dept_id = 1` after reloading.

::: info If an Entity object is referenced by two Mappers, both Mappers will update the corresponding Entity class information when they are executed.

```java
@Mapper
public interface User1Dao extends BaseMapper<User> {

}

@Mapper
public interface User2Dao extends BaseMapper<User> {

}
```
:::

::: tip Changes to Entity information will not update the hard-coded information in Mapper. For example, the following **getAll** method will output **select id, deptId, name, sex from user** regardless of how User is updated.

```java
@Mapper
public interface UserDao extends BaseMapper<User> {
    
    @Select("select id, deptId, name, sex from user") // [!code focus]
    List<User> getAll(); // [!code focus]
}
```

::: 

## Mapper Hot Reload

### Identification method

All files under the package path annotated with `@MapperScan` are read.

- Must be an interface and must contain the `org.apache.ibatis.annotations.Mapper` annotation
- The class or parent class inherits the `com.baomidou.mybatisplus.core.mapper.BaseMapper` interface.

::: tip

Because the bytecode injection is modified at startup, it currently does not support reading the variable content configured by `@MapperScan`. Please write the real package path.

Effective configurations such as:
@MapperScan("io.github.future0923.test.dao")
@MapperScan("io.github.future0923.test.**.dao")
@MapperScan("io.github.future0923.test.**")
@MapperScan({"io.github.future0923.test.user.**.dao", "io.github.future0923.test.order.**.dao"})

Unsupported configurations such as:
@MapperScan("${mybatis-plus.mapperPackage}")

:::

### Expected Results

The addition and modification of the Mapper interface will regenerate the information in the Mapper interface, including **default methods**, **annotation methods**, etc. The proxy class will be regenerated and injected into the Spring Bean, which has the same effect as restarting. **`Of course, adding new Mapper classes`** is also supported.

### Example

#### Add {#add-mapper}

**Add entity file**。

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

**Add mapper file**。

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

Add a new Service file to use the `UserDao` interface.

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

After hot reloading, we can call the newly added methods of UserDao and UserService through [call method function](attach-local.md) and they can be executed normally. 

#### Modify

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

- You can execute the newly added `getAll()` method, and output `select id, deptId, user_name, sex from user`.
- You can execute the newly added `getByDeptId(1L)` method, and output `select id, dept_id, user_name, age from user where dept_id = 1`.
- The previous `getUserCountByName()` method no longer exists. You can execute the modified `getUserCountBySex()` method, and output `select sex, count(1) from user group by sex`.
- The previous `getByUserName(String userName)` method no longer exists. You can execute the modified `getBySex(Integer sex)` method, and output `select id, dept_id, user_name, age from user where sex = xxx`.
