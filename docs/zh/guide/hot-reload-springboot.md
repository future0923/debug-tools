# SpringBoot热重载 <Badge type="warning" text="beta" />

- 由于目前没有做Xml文件的更新识别，**Spring项目的Xml配置新增的Bean无法识别**，但是已经注册的Bean上可以热重载。

## Bean热重载

核心通过解析类文件信息生成 `BeanDefinition`，调用 `ClassPathBeanDefinitionScanner.registerBeanDefinition()` 注册到 Spring 容器中。 理论上支持所有被 Spring 管理的 Bean 变动都可以热重载，当然也支持新增 Bean。

### Controller

Controller层类的新增或修改可以进行热重载并会重新解析 Mapping 注解（如 @RequestMapping、@GetMapping、@PostMapping 等）信息。

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

- 之前的 `/user/addUser` 无法访问了，可以访问新增的 `/user/saveUser`。
- 可以访问新增的 `/user/order`，使用到的 `UserService` 和 `OrderService` 也会注入到 `UserController` 中。

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

- 可以调用新增的 `deleteUser()`
- 之前的 `getUser()`方法无法访问，可以访问新增的 `selectUser()` 方法。

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

- 可以调用新增的 `deleteUser()` 
- 之前的 `getUser()`方法无法访问，可以访问新增的 `selectUser()` 方法。

### Repository

目前支持 MybatisPlus 的热重载，详细点击 [MyBatisPlus热重载](hot-reload-mybatis-plus.md) 查看

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

- 载入新增的 `user.name` 配置
- 载入修改的 `user.image` 配置

```java
@Configuration
public class UserConfig {
    
    @Bean// [!code ++]
    public UserBean userBean() {// [!code ++]
        return new UserBean();// [!code ++]
    }// [!code ++]
}
```

- 注入新增的 `UserBean` Bean

### 抽象类

当抽象类修改的时候，所有继承的子类 Bean 都会进行热重载。

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

当抽象类 `User` 热重载之后，`DebugUser` 和 `ToolsUser` 都会进行热重载并可以使用 `userDao` 属性和 `getUser(Long userId)` 方法。

### 接口

接口默认方法修改，所有的实现类 bean 都会生效。

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

当接口 `User` 热重载之后，`DebugUser` 和 `ToolsUser` 都会进行热重载并可以 `getDefaultUser()` 方法。

### 等等

理论上被Spring Bean管理的业务所有类都可以进行热重载。