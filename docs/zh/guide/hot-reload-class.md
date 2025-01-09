# 普通class文件热重载

## 属性变动

```java
public class User {
    
    private String userId; // [!code ++]
    
    private String name; // [!code --]
    private String userName; // [!code ++]
    
    private Integer userAge; // [!code --]
}
```

- 可以使用新增的 `userId` 属性。
- 删除 `name` 属性，可以使用新增的 `userName` 属性。
- 可以使用新增的 `userAge` 属性。

## 方法变动

```java
public class UserUtils {

    public static String getUserName(UserVO userVO) {// [!code ++]
        return userVO.getName();// [!code ++]
    }// [!code ++]
    
    public static User getDefaultUser() { // [!code --]
    public static User genDefaultUser () { // [!code ++]
        User user = new User();
        user.setUserId(0); // [!code --]
        user.setUserId(Id.next()); // [!code ++]
        user.setName("default"); // [!code --]
        user.setUserName("default"); // [!code ++]
        user.setUserAge(0);// [!code --]
        return user;
    }
}
```

- 可以使用新增的 `getUserName` 方法。
- 删除掉 `getUserDefaultUser` 方法，可以使用新增的 `genDefaultUser` 方法。

## 内部类变动

```java
public class UserVO {
    
    private String userName;
    
    private List<RoleVO> userRoles;
    
    public static class RoleVO {
        
        private Long roleId;  // [!code --]
        
        private String name;  // [!code --]
        private String roleName;  // [!code ++]

        private Integer status; // [!code ++]
        
        public String getName() { // [!code --]
            return name; // [!code --]
        public String getRoleName() { // [!code ++]
            return roleName; // [!code ++]
        }
        
        public String getStatusName() { // [!code ++]
            return status == 1 ? "正常" : "禁用"; // [!code ++]
        } // [!code ++]
        
    }
    
    private List<AuthVO> userAuths; // [!code ++]
    
    public class AuthVO { // [!code ++]
        
        private String authName; // [!code ++]
        
    } // [!code ++]
}
```

- 增加了 `userAuths` 属性。
- 正常使用修改了 `RoleVO` 内部类信息。
- 正常使用新增了 `AuthVO` 内部类信息。

## 抽象类

开启热重载，抽象类修改，所有的子类都会生效。

```java
public abstract class User {
    
    public String getUserName() { // [!code ++]
        return "default"; // [!code ++]
    } // [!code ++]
}

public class Debug extends User {
    
}

public class Tools extends User {

}
```

热重载 `User` 之后，`Debug` 和 `Tools` 都可以使用 `getUserName()` 方法。

## 接口

接口默认方法修改，所有的实现类都会生效。

```java
public interface User {
    
    default String getUserName() { // [!code ++]
        return "default"; // [!code ++]
    } // [!code ++]
}

public class Debug implements User {
    
}

public class Tools implements User {

}
```

热重载 `User` 之后，`Debug` 和 `Tools` 都可以使用 `getUserName()` 方法。