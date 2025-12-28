# Debug Tools 开发文档

## 📖 概述

Debug Tools 是一个集成于 IntelliJ IDEA 的插件，专注于提升 Java 开发效率和缩短调试周期。项目采用 Java Agent 技术实现运行时字节码修改，提供热部署、热重载、远程调试等强大功能。

## 🏗️ 项目架构

### 模块结构

```
debug-tools/
├── debug-tools-idea/          # IntelliJ IDEA 插件实现
├── debug-tools-server/        # 服务器端实现
├── debug-tools-client/        # 客户端通信模块
├── debug-tools-core/          # 核心功能模块
├── debug-tools-hotswap/       # 热部署核心功能
├── debug-tools-attach/        # Agent 入口点
├── debug-tools-sql/           # SQL 监控功能
├── debug-tools-base/          # 基础工具类
├── debug-tools-common/        # 公共模块
├── debug-tools-vm/            # JVM 工具
├── debug-tools-extension/     # 框架扩展
└── debug-tools-test/          # 测试模块
```

### 技术栈

- **编程语言**: Java
- **构建工具**: Maven + Gradle
- **主要框架**: Spring Boot, Mybatis
- **字节码操作**: Javassist, Java Instrumentation API
- **通信协议**: TCP + HTTP
- **IDE 支持**: IntelliJ IDEA 2023.1+

## ⚡ 核心功能

### 1. 热部署 (Hot Deployment)
```java
// 传统流程: 提交代码 -> 拉取代码 -> 打包 -> 部署 -> 重启
// Debug Tools: 修改代码 -> 实时生效
```

### 2. 热重载 (Hot Reload)
- 支持类属性和方法变动
- 框架支持: Spring、Solon、Mybatis、Hibernate Validator
- 代理类支持: CGLib、JDK 动态代理

### 3. 方法调用调试
```java
// 直接调用任意Java方法，无需通过Controller层
DebugTools.invokeMethod("com.example.Service", "methodName", params);
```

### 4. 远程调试
- 支持远程方法调用
- 配合远程 Debug 实现完整调试流程
- TCP/HTTP 双协议支持

### 5. SQL 监控
- 无侵入式 SQL 语句记录
- 执行耗时统计
- 支持多种数据库

### 6. URL 搜索
- 智能 URL 解析
- 直接定位到方法定义
- 支持 Spring MVC 注解

### 7. XXL-Job 集成
- 直接调用客户端方法
- 上下文参数传递
- 任务调试支持

### 8. Groovy 脚本执行
```groovy
// 执行Groovy脚本调试
def result = executeScript("println 'Hello DebugTools'")
```

## 🔧 技术实现

### Java Agent 架构
```java
public class DebugToolsAttach {
    // Agent 入口点
    public static void premain(String agentArgs, Instrumentation inst) {
        // 初始化逻辑
        HotswapAgent.init(parse, inst);
        DebugToolsBootstrap.getInstance(inst).start(agentArgs);
    }
}
```

### 热重载实现原理
1. **文件监听**: 使用 NIO.2 WatchService 监控文件变化
2. **字节码修改**: Javassist 动态修改类字节码
3. **类重定义**: Instrumentation#redefineClasses
4. **框架适配**: Spring Bean 重新加载机制

### 通信架构
- **TCP Server**: 高性能二进制协议通信
- **HTTP Server**: RESTful API 接口
- **客户端**: IDEA 插件集成界面

## 📦 安装配置

### 环境要求
- JDK 8+
- IntelliJ IDEA 2023.1+
- Maven 3.6+ 或 Gradle 7+

### 插件安装
1. 从 JetBrains 插件市场安装 Debug Tools
2. 或手动构建安装:
```bash
cd debug-tools-idea
./gradlew buildPlugin
```

### 项目配置
```yaml
# application.yml 配置示例
debug:
  tools:
    enabled: true
    hotswap: true
    tcp-port: 12345
    http-port: 22222
```

## 🚀 使用指南

### 基本使用
1. **安装插件**: 在 IDEA 中安装 Debug Tools 插件
2. **启动应用**: 使用 Debug Tools 代理启动应用
3. **连接调试**: 通过插件界面连接目标应用
4. **实时修改**: 修改代码后实时生效

### 热部署示例
```java
// 修改前
public String hello() {
    return "Hello World";
}

// 修改后 - 无需重启立即生效
public String hello() {
    return "Hello DebugTools";
}
```

### 方法调用示例
```java
// 在IDEA中直接调用方法
DebugTools.callMethod(
    "com.example.UserService", 
    "getUserById", 
    new Object[]{123L}
);
```

## 🧪 测试支持

项目提供完整的测试套件，包含:

### 测试模块
- **debug-tools-test-simple**: 基础功能测试
- **debug-tools-test-application**: 完整应用测试
- **debug-tools-test-agent**: Agent 功能测试
- **debug-tools-test-spring-***: 框架集成测试

### 测试用例覆盖
- 热重载功能测试
- 方法调用测试
- SQL 监控测试
- 跨版本兼容性测试

## 🔍 性能优化

### 内存优化
- 使用弱引用管理临时对象
- 字节码缓存机制
- 连接池资源管理

### 执行效率
- 异步命令执行
- 批量类重定义
- 增量热更新

## 🛠️ 开发指南

### 代码规范
- 遵循 Google Java 代码风格
- 使用 Lombok 减少样板代码
- 完整的注释文档

### 构建部署
```bash
# 完整构建
mvn clean install

# 单独构建插件
cd debug-tools-idea
gradle buildPlugin

# 发布到本地仓库
mvn deploy
```

### 调试开发
1. 启动测试应用
2. 附加 Debug Tools Agent
3. 使用 IDEA 远程调试
4. 实时修改测试

## ?? 监控指标

### 性能监控
- 热重载执行时间
- 方法调用响应时间
- 内存使用情况
- 连接数统计

### 日志管理
- 分级日志输出
- 运行状态监控
- 错误预警机制

## 🌟 最佳实践

### 开发环境
- 使用 JDK 11+ 获得更好的性能
- 配置足够的内存空间
- 启用详细日志便于调试

### 生产环境
- 合理配置热重载开关
- 监控资源使用情况
- 定期更新到最新版本

## 📝 版本历史

### v4.5.0 (当前)
- 支持 JDK 21-25
- 增强热重载稳定性
- 优化内存使用

### v4.0.0
- 重构架构模块化
- 新增 SQL 监控功能
- 支持更多框架

## 🆘 常见问题

### Q: 热重载不生效怎么办？
A: 检查类加载器配置，确保使用 JDK 环境而非 JRE

### Q: 远程连接失败？
A: 检查防火墙设置和目标应用网络可达性

### Q: 性能下降明显？
A: 调整热重载阈值，避免过于频繁的类重定义

## 📞 技术支持

- **文档**: https://debug-tools.cc
- **GitHub**: https://github.com/future0923/debug-tools
- **Issues**: https://github.com/future0923/debug-tools/issues
- **QQ群**: 518757118
- **邮箱**: future94@qq.com

## 📄 许可证

本项目采用 GPL 3.0 许可证开源，允许自由使用、修改和分发，但需要遵循许可证条款。

---
*最后更新: 2025-12-28*
*文档版本: v1.0*