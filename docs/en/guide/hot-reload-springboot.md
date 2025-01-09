# SpringBoot Hot Reload <Badge type="warning" text="beta" />

- Since there is no update recognition for XML files at present, **the newly added Beans in the XML configuration of the Spring project cannot be recognized**, but hot reload can be performed on the already registered Beans.

## Bean Hot Reload

The core generates `BeanDefinition` by parsing class file information, and calls `ClassPathBeanDefinitionScanner.registerBeanDefinition()` to register it in the Spring container. In theory, all Bean changes managed by Spring can be hot-reloaded, and of course, new Beans are also supported.

### Controller

Additions or modifications to Controller layer classes can be hot-reloaded and Mapping annotations (such as @RequestMapping, @GetMapping, @PostMapping, etc.) information will be reparsed.

```java
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserComponent userComponent; // [!code ++]
    
    private final OrderService orderService; // [!code ++]
    
    public UserController(UserComponent userComponent) { // [!code ++]
        this.userComponent = userComponent; // [!code ++]
    } // [!code ++]

    @GetMapping("/addUser") // [!code --]
    public String addUser() { // [!code --]
        return "addUser"; // [!code --]
    @GetMapping("/saveUser") // [!code ++]
    public String saveUser() { // [!code ++]
        return "saveUser"; // [!code ++]
    }

    @GetMapping("/order") // [!code ++]
    public String order(@RequestParam("userId") Long userId) { // [!code ++]
        User user = userComponent.selectUser(userId); // [!code ++]
        if (user == null) { // [!code ++]
            return "user is null";  // [!code ++]
        } // [!code ++]
        return orderService.order(user); // [!code ++]
    } // [!code ++]
}
```

- The previous `/user/addUser` is no longer accessible, but the newly added `/user/saveUser` can be accessed.
- The newly added `/user/order` can be accessed, and the used `UserService` and `OrderService` will also be injected into `UserController`.

### Component

```java

@Service
public class UserComponent {
    
    private final UserService userService;
    
    public UserComponent(UserService userService) {
        this.userService = userService;
    }

    public User deleteUser(Long userId) { // [!code ++]
        return userService.selectUser(userId);// [!code ++]
    }// [!code ++]

    public User getUser(Long userId) {// [!code --]
        return userService.getUser(userId);// [!code --]
    public User selectUser(Long userId) {// [!code ++]
        return userService.selectUser(userId);// [!code ++]
    }
}
```

- You can call the newly added `deleteUser()`
- The previous `getUser()` method cannot be accessed, but the newly added `selectUser()` method can be accessed.

### Service

```java

@Service
public class UserService {
    
    private final UserDao userDao;
    
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User deleteUser(Long userId) { // [!code ++]
        return userDao.selectUser(userId);// [!code ++]
    }// [!code ++]

    public User getUser(Long userId) {// [!code --]
        return userDao.getUser(userId);// [!code --]
    public User selectUser(Long userId) {// [!code ++]
        return userDao.selectUser(userId);// [!code ++]
    }
}
```

- You can call the newly added `deleteUser()`
- The previous `getUser()` method cannot be accessed, but the newly added `selectUser()` method can be accessed.

### Repository

Currently supports MybatisPlus hot reload, for details, click [MyBatisPlus hot reload](hot-reload-mybatis-plus.md) to view

### Configuration

```java
@Configuration
public class UserConfig {

    @Value("${user.name}") // [!code ++]
    private String userName;// [!code ++]

    @Value("${user.avatar}") // [!code --]
    private String userAvatar;// [!code --]
    @Value("${user.image}") // [!code ++]
    private String userImage;// [!code ++]
}
```

- Load the newly added `user.name` configuration
- Load the modified `user.image` configuration

```java
@Configuration
public class UserConfig {
    
    @Bean// [!code ++]
    public UserBean userBean() {// [!code ++]
        return new UserBean();// [!code ++]
    }// [!code ++]
}
```

- Inject the newly added `UserBean` Bean


### Abstract Class

When the abstract class is modified, all inherited subclass beans will be hot reloaded.

```java
public abstract class User {
    
    @Resource // [!code ++]
    protected UserDao userDao; // [!code ++]
    
    public User getUser(Long userId) { // [!code ++]
        return userDao.getUser(userId); // [!code ++]
    } // [!code ++]
    
}

@Component
public class DebugUser extends User {
    
}

@Component
public class ToolsUser extends User {

}
```

When the abstract class `User` is hot reloaded, both `DebugUser` and `ToolsUser` will be hot reloaded and can use the `userDao` property and the `getUser(Long userId)` method.

### Interface

When the default method of an interface is modified, the changes will take effect on all beans in the implementation class.

```java
public interface User {
    
    default User getDefaultUser() { // [!code ++]
        return new DebugUser(); // [!code ++]
    } // [!code ++]
    
}

@Component
public class DebugUser implements User {
    
}

@Component
public class ToolsUser implements User {

}
```

When the interface `User` is hot-reloaded, `DebugUser` and `ToolsUser` will be hot-reloaded and can use the `getDefaultUser()` method.

### More

In theory, all business classes managed by Spring Bean can be hot-reloaded.