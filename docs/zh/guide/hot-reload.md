# 热重载 <Badge type="warning" text="beta" /> {#hot-reload} 

无需重启应用即可让编写的代码生效，支持类(包括代理类)的属性和方法变动、SpringBoot、MybatisPlus等，提升开发效率。

## 开启热重载

点击 <img src="/icon/hotswap.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 图标变为 <img src="/icon/hotswap_on.svg" style="display: inline-block; width: 25px; height: 25px; vertical-align: middle;" /> 表示开启热重载，大飞机模式下启动项目热重载即可生效。

- 关闭状态

![hotswap_off](/images/hotswap_off.png){v-zoom}

- 开启状态

![hotswap_on](/images/hotswap_on.png){v-zoom}

启动项目如果提示 `DCEVM is not installed` ，检查命令 `java -XXaltjvm=dcevm -version` 是否能正常输出。如果不能则需要先[安装 DCEVM](#install-dcevm)。

![dcevm_not_install.png](/images/dcevm_not_install.png){v-zoom}

## 使用热重载

在大飞机的状态下启动项目，项目输出如下日志，并打印载入的热重载插件。

```text
DebugTools: 2025-01-07 16:41:07.909    INFO [main] i.g.f.d.t.h.c.HotswapAgent 44 : open hot reload unlimited runtime class redefinition.{3.2.0}
DebugTools: 2025-01-07 16:41:08.498    INFO [main] i.g.f.d.t.h.c.c.PluginRegistry 132 : Discovered plugins: [JdkPlugin, ClassInitPlugin, AnonymousClassPatch, WatchResources, HotSwapper, Proxy, Spring, MyBatis]
```

编写代码后通过 idea 编译项目后自动触发热重载（idea快捷键自己探索）。

- `Run` 方式启动项目可以通过如下方式编译项目触发热重载

![build_project.png](/images/build_project.png){v-zoom}

- `Debug` 方式启动项目不但可以通过上面 **Run** 的方式编译项目触发热重载，还可以通过右键菜单的 `Compile and Reload Modified Files` 方式重新编译 class 触发热重载.

![compile_reload_file.png](/images/compile_reload_file.png)

控制台会输出相关热重载的信息。

```text
DebugTools: 2025-01-07 16:50:22.205  RELOAD [Thread-26] i.g.f.d.t.h.c.p.s.s.ClassPathBeanDefinitionScannerAgent 210 : Registered Spring bean 'testController'
```

## 哪些情况可以热重载

### 普通的class文件

- 新增类文件
- 存在的类 **增加/修改** 类中的 **属性/方法/内部类**。

详细点击 [class文件热重载](hot-reload-class.md) 查看

### 代理类

- java JDK 代理类。
- Cglib 代理类。

详细点击 [代理类热重载](hot-reload-proxy.md) 查看

### SpringBoot Bean

- Controller
- Service
- Component
- Repository

详细点击 [SpringBoot](hot-reload-springboot.md) 查看

### MyBatis Plus

- entity
- mapper

详细点击 [MyBatisPlus](hot-reload-mybatis-plus.md) 查看

## 安装 DCEVM {#install-dcevm}

### java 8

#### window/mac

下载对应版本的 .jar 文件。

| java version | download by debug tools                                                                                | [download by github](https://github.com/future0923/debug-tools/releases/tag/dcevm-installer)                                       |
|--------------|--------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| 1.8.0_172    | [DCEVM-8u172-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u172-installer.jar) | [DCEVM-8u172-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u172-installer.jar) |
| 1.8.0_152    | [DCEVM-8u152-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u152-installer.jar) | [DCEVM-8u152-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u152-installer.jar) |
| 1.8.0_144    | [DCEVM-8u144-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u144-installer.jar) | [DCEVM-8u144-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u144-installer.jar) |
| 1.8.0_181    | [DCEVM-8u181-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u181-installer.jar) | [DCEVM-8u181-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u181-installer.jar) |
| 1.8.0_112    | [DCEVM-8u112-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u112-installer.jar) | [DCEVM-8u112-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u112-installer.jar) |
| 1.8.0_92     | [DCEVM-8u92-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u92-installer.jar)   | [DCEVM-8u92-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u92-installer.jar)   |
| 1.8.0_74     | [DCEVM-8u74-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u74-installer.jar)   | [DCEVM-8u74-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u74-installer.jar)   |
| 1.8.0_66     | [DCEVM-8u66-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u66-installer.jar)   | [DCEVM-8u66-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u66-installer.jar)   |
| 1.8.0_51     | [DCEVM-8u51-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u51-installer.jar)   | [DCEVM-8u51-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u51-installer.jar)   |
| 1.8.0_45     | [DCEVM-8u45-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u45-installer.jar)   | [DCEVM-8u45-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u45-installer.jar)   |

运行对应的 `java -jar DCEVM-8uXX-installer.jar` 文件，找到对应的版本，点击 `Install DCEVM as altjvm` 按钮即可。

![dcevm-installer.png](/images/dcevm-installer.png){v-zoom}

#### linux

如输入 `java -XXaltjvm=dcevm -version` 输入如下提示

```text
Error: missing `dcevm' JVM at `/home/java/jdk1.8.0_291/jre/lib/amd64/dcevm/libjvm.so'.
Please install or use the JRE or JDK that contains these missing components.
```

下载对应版本的文件并改名为 `libjvm.so` 到上面提取的目录下即可。

| java version | download by debug tools                                             | [download by github](https://github.com/future0923/debug-tools/releases/tag/libjvm.so)             |
|--------------|---------------------------------------------------------------------|----------------------------------------------------------------------------------------------------|
| >= 1.8.0_181 | [libjvm181.so](https://download.debug-tools.cc/libjvm/libjvm181.so) | [libjvm181.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm181.so) |
| 1.8.0_172    | [libjvm172.so](https://download.debug-tools.cc/libjvm/libjvm172.so) | [libjvm172.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm172.so) |
| 1.8.0_152    | [libjvm152.so](https://download.debug-tools.cc/libjvm/libjvm152.so) | [libjvm152.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm152.so) |
| 1.8.0_144    | [libjvm144.so](https://download.debug-tools.cc/libjvm/libjvm144.so) | [libjvm144.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm144.so) |
| 1.8.0_112    | [libjvm112.so](https://download.debug-tools.cc/libjvm/libjvm112.so) | [libjvm112.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm112.so) |
| 1.8.0_92     | [libjvm92.so](https://download.debug-tools.cc/libjvm/libjvm92.so)   | [libjvm92.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm92.so)   |
| 1.8.0_74     | [libjvm74.so](https://download.debug-tools.cc/libjvm/libjvm74.so)   | [libjvm74.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm74.so)   |
| <= 1.8.0_66  | [libjvm66.so](https://download.debug-tools.cc/libjvm/libjvm66.so)   | [libjvm66.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm66.so)   |


### java 11

下载 [trava-jdk-11-dcevm](https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases) 作为jdk11使用。

### java 17/21

下载 [JetBrainsRuntime](https://github.com/JetBrains/JetBrainsRuntime/releases) 作为jdk17/21使用。

:::tip

苹果系统如果下载JDK后提示已损坏或无法验证开发者等原因不能启动JDK，输入 `sudo xattr -r -d com.apple.quarantine /$jdkPath` 即可， **$jdkPath** 是你的jdk目录

:::




