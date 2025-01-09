# Hot reload of normal class files

## Property changes

```java
public class User {
    
    private String userId; // [!code ++]
    
    private String name; // [!code --]
    private String userName; // [!code ++]
    
    private Integer userAge; // [!code --]
}
```

- You can use the new `userId` attribute.
- Remove the `name` attribute, and use the new `userName` attribute.
- You can use the new `userAge` attribute.

## Method changes

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

- You can use the new `getUserName` method.
- Remove the `getUserDefaultUser` method, and use the new `genDefaultUser` method.

## Internal class changes

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

- Added `userAuths` attribute.
- Modified `RoleVO` internal class information for normal use.
- Added `AuthVO` internal class information for normal use.

## Abstract class

Turn on hot reload, and when the abstract class is modified, all subclasses will take effect.

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

After hot reloading `User`, both `Debug` and `Tools` can use the `getUserName()` method.

## Interface

When the default method of an interface is modified, all implementation classes will take effect.

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

After hot reloading `User`, both `Debug` and `Tools` can use the `getUserName()` method.