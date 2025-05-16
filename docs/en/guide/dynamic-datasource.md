# DynamicDatasource

- Support [dynamic-datasource](https://github.com/baomidou/dynamic-datasource) dynamic data source `@DS` annotation hot reload
- Support MyBatis and MyBatisPlus and other ORM frameworks

## Add

```java
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("source1")
public interface UserMapper {

    @Select("select user_name, count(1) from user group by user_name")
    Map<String, Integer> getUserCountByName();
}
```

After hot reload, execute the newly added `UserMapper#getUserCountByName` to use the `source1` data source.

## Modify

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

After hot reload, execute the newly added `UserMapper#getUserCountByName` to switch from `source1` to `source2` data source.