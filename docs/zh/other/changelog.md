---
layout: doc
aside: false
---
# 版本迭代记录

## [4.0.0-SNAPSHOT](https://github.com/future0923/debug-tools/compare/v3.4.4...main)

- 增加热部署功能，实现秒级一键热更新远程应用代码。
- 增加远程动态编译功能，热部署、热重载时可以选择本地Idea编译还是远程应用动态编译代码
- 增加默认类加载器选择功能，执行热部署、热重载、Groovy脚本时可以选择类加载器
- 增加[dynamic-datasource](https://github.com/baomidou/dynamic-datasource)动态数据源 `@DS` 注解热重载([#8](https://github.com/future0923/debug-tools/issues/8))
- 增加[hutool](https://hutool.cn)工具包热重载([#48](https://github.com/future0923/debug-tools/issues/48))
- 增加[Gson](https://github.com/google/gson)工具包热重载([#49](https://github.com/future0923/debug-tools/issues/49))
- 增加[EasyExcel](https://github.com/alibaba/easyexcel)工具包热重载([#46](https://github.com/future0923/debug-tools/issues/46))
- 重构调用方法功能，远程调用支持热部署后类
- 支持调用两级以上内部类方法([#43](https://github.com/future0923/debug-tools/issues/43)).
- 优化心跳逻辑
- 优化 http 超时时间
- 修复RuntimeExceptionWithAttachments：Read access is allowed from inside read-action only的BUG([#38](https://github.com/future0923/debug-tools/issues/38))

## [3.4.4](https://github.com/future0923/debug-tools/compare/v3.4.3...v3.4.4) (2025-04-22)

- 支持 intellij idea 2025.1 版本([#37](https://github.com/future0923/debug-tools/issues/37)).

## [3.4.3](https://github.com/future0923/debug-tools/compare/v3.4.2...v3.4.3) (2025-04-01)

- 修复文件夹下只有一个 Controller 文件时改名热重载失败的BUG([#31](https://github.com/future0923/debug-tools/issues/31)).
- 修改 JDK21 热重载启动失败的BUG([#30](https://github.com/future0923/debug-tools/issues/30)).
- 修改 WebFlux 无法调用的BUB([#29](https://github.com/future0923/debug-tools/issues/29)).

## [3.4.2](https://github.com/future0923/debug-tools/compare/v3.4.1...v3.4.2) (2025-03-17)

- 增加按钮触发编译 XML 文件以触发([#21](https://github.com/future0923/debug-tools/issues/21)).
- 修复 Controller 文件重命名时无法触发热重载的BUG([#23](https://github.com/future0923/debug-tools/issues/23)).
- 修复连接远程应用失败的BUG([#27](https://github.com/future0923/debug-tools/issues/27)).

## [3.4.1](https://github.com/future0923/debug-tools/compare/v3.4.0...v3.4.1) (2025-02-27)

- 修复多个 MyBatis Mapper 热重载时 ConcurrentModificationException 的BUG([#20](https://github.com/future0923/debug-tools/issues/20)).

## [3.4.0](https://github.com/future0923/debug-tools/compare/v3.3.0...v3.4.0) (2025-02-20)

- 热重载支持支持 static 变量、static final 变量、static 代码块([#10](https://github.com/future0923/debug-tools/issues/10)).
- 热重载支持支持 Enum([#10](https://github.com/future0923/debug-tools/issues/10))
- 修复在Spring环境中无法调用非Bean类的BUG([#16](https://github.com/future0923/debug-tools/issues/16)).

## [3.3.0](https://github.com/future0923/debug-tools/compare/v3.2.0...v3.3.0) (2025-02-11)

- 热重载支持 MyBatis Plus 增加Entity、修改Entity、增加Mapper、修改Mapper、增加xml、修改xml.
- 热重载支持 MyBatis 增加Mapper、修改Mapper、增加xml、修改xml.
- 增加通过默认类加载器执行上一次功能([#12](https://github.com/future0923/debug-tools/issues/12)).
- 修复开启SQL打印时无法执行Maven命令的BUG([#7](https://github.com/future0923/debug-tools/issues/7)).

## [3.2.0](https://github.com/future0923/debug-tools/compare/v3.1.2...v3.2.0) (2025-01-09)

- 增加热重载功能，无需重启应用即可使编写的代码生效。

## [3.1.2](https://github.com/future0923/debug-tools/compare/v3.1.1...v3.1.2) (2024-12-30)

- 修复移除 ContextPath 失败的BUG([#3](https://github.com/future0923/debug-tools/issues/3)).
- 修复多个项目共享一个附加按钮的 bug([#4](https://github.com/future0923/debug-tools/issues/4)).

## [3.1.1](https://github.com/future0923/debug-tools/compare/v3.1.0...v3.1.1) (2024-12-18)

- 搜索 http url时可以移除 ContextPath 信息
- 修复没有 http(s) 时无法匹配域名的BUG

## [3.1.0](https://github.com/future0923/debug-tools/compare/v3.0.1...v3.1.0) (2024-12-17)

- 增加搜索 http url功能.
- 修复远程应用时 Groovy 执行时无法获取默认类加载器的BUG.
- 修复 DebugToolsClassLoader 重复加载 AppClassLoader 基础包导致使用 DefaultClassLoader 抛出异常的BUG。
- 优化日志打印([#2](https://github.com/future0923/debug-tools/issues/2))

## [3.0.1](https://github.com/future0923/debug-tools/compare/v3.0.0...v3.0.1) (2024-12-12)

- 支持 intellij idea 2024.3 版本.
- 修复远程应用时 Groovy 执行时无法获取默认类加载器的BUG.

## 3.0.0 (2024-11-01)

- 增加可以调用远程应用方法的功能.
- 增加方法调用时选择类加载器的功能.

## 2.3.0 (2024-09-19)

- 增加groovy控制台，可以通过目标应用快捷执行代码
- 增加 debug 方式可以快捷复制name和value
- 增加 attach 应用可以断开链接和停止服务

## 2.2.0 (2024-08-28)

- 支持 toString、json、debug 的方式查看返回结果
- 支持 console、debug 的方式查看异常信息
- 支持 org.springframework.web.multipart.MultipartFile 类型参数传入
- 优化插件样式
- 修复参数嵌套时的递归堆栈溢出异常

## 2.1.0 (2024-08-19)

- 支持 2024.2 版本
- 支持非Spring环境的调用
- 支持传递 xxl-job 参数
- 支持更多的参数传递
  - request (MockHttpServletRequest)
  - response (MockHttpServletResponse)
  - file (absolute path to File object)
  - class (class full name to Class object)
- 优化窗口参数逻辑

## 2.0.1 (2024-08-09)

- 修复心跳太短导致业务执行中断的BUG
- 修复异常信息打印不完整的BUG

## 2.0.0 (2024-08-06)

- 增加交互逻辑，支持调用方查看执行结果和错误信息.
- 修复NULL时SQL打印显示错误的BUG.

## 1.2.1 (2024-06-13)

- 增加缓存清理逻辑
- 增加执行失败的异常返回信息
- 优化请求头参数传入逻辑
- 修复批量操作无法打印SQL的BUG

## 1.2.0 (2024-06-07)

- 优化核心类库加载逻辑
- 增加打印SQL语句与耗时功能
- 修复window下类库加载失败的BUG

## 1.1.1 (2024-06-04)

- 增加自定义类加载器隔目标应用
- 修复集合类型时 StackOverflowError 的BUG

## 1.1.0 (2024-05-30)

- 增加调用配置信息
- 增加请求头参数传递

## 1.0.0 (2024-05-29)

- 可以调用任意Java方法并支持参数传递

## 第一行代码提交 (2024-05-08)