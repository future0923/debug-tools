# DynamicDatasource

- 支持 [dynamic-datasource](https://github.com/baomidou/dynamic-datasource) 动态数据源 `@DS` 注解热重载
- 同时支持MyBatis与MyBatisPlus等ORM框架

## 新增

```java
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("source1")
public interface UserMapper {

    @Select("select user_name, count(1) from user group by user_name")
    Map<String, Integer> getUserCountByName();
}
```

热重载之后执行新增的 `UserMapper#getUserCountByName` 使用 `source1` 数据源。

## 修改

```java
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("source1") // [!code --]
@DS("source2") // [!code ++]
public interface UserMapper {

    @Select("select user_name, count(1) from user group by user_name")
    Map<String, Integer> getUserCountByName();
}
```

热重载之后执行新增的 `UserMapper#getUserCountByName` 从 `source1` 切换到 `source2` 数据源。