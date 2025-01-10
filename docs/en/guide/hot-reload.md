# Hot Reload <Badge type="warning" text="beta" /> {#hot-reload} 

The written code can be made effective without restarting the application. It supports changes in properties and methods of classes (including proxy classes), SpringBoot, MybatisPlus, etc., to improve development efficiency.

## Enable hot reload

Click <img src="/icon/hotswap.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> The icon changes to <img src="/icon/hotswap_on.svg" style="display: inline-block; width: 25px; height: 25px; vertical-align: middle;" /> to turn on hot reload. Start the project in the big airplane mode and hot reload will take effect.

- Off status

![hotswap_off](/images/hotswap_off.png){v-zoom}

- On status

![hotswap_on](/images/hotswap_on.png){v-zoom}

If the prompt `DCEVM is not installed` is prompted when starting the project, check whether the command `java -XXaltjvm=dcevm -version` can be output normally. If not, you need to [install DCEVM](#install-dcevm) first.

![dcevm_not_install.png](/images/dcevm_not_install.png){v-zoom}

## Use hot reload

Start the project in the state of the big plane, the project outputs the following log, and prints the loaded hot reload plug-in.

```text
DebugTools: 2025-01-07 16:41:07.909    INFO [main] i.g.f.d.t.h.c.HotswapAgent 44 : open hot reload unlimited runtime class redefinition.{3.2.0}
DebugTools: 2025-01-07 16:41:08.498    INFO [main] i.g.f.d.t.h.c.c.PluginRegistry 132 : Discovered plugins: [JdkPlugin, ClassInitPlugin, AnonymousClassPatch, WatchResources, HotSwapper, Proxy, Spring, MyBatis]
```

After writing the code, hot reload is automatically triggered after compiling the project through idea (explore the idea shortcut keys yourself).

- Start the project in `Run` mode. You can compile the project in the following way to trigger hot reload

![build_project.png](/images/build_project.png){v-zoom}

- Start the project in `Debug` mode. Not only can you compile the project in the above **Run** way to trigger hot reload, you can also recompile the class in the `Compile and Reload Modified Files` way in the right-click menu to trigger hot reload.

![compile_reload_file.png](/images/compile_reload_file.png)

The console will output relevant hot reload information.

```text
DebugTools: 2025-01-07 16:50:22.205  RELOAD [Thread-26] i.g.f.d.t.h.c.p.s.s.ClassPathBeanDefinitionScannerAgent 210 : Registered Spring bean 'testController'
```
## In what situations can hot reload be used

### Ordinary class files

- Add new class files
- **Add/modify** **properties/methods/inner classes** in existing classes.

Click [Hot reload of class files](hot-reload-class.md) for details

### Proxy class

- Java JDK proxy class.

- Cglib proxy class.

Click [Hot reload proxy class](hot-reload-proxy.md) for details

### SpringBoot Bean

- Controller
- Service
- Component
- Repository

Click [SpringBoot](hot-reload-springboot.md) for details

### MyBatis Plus

- entity
- mapper

Click [MyBatisPlus](hot-reload-mybatis-plus.md) for details

## Install DCEVM {#install-dcevm}

### java 8

#### window/mac

Download the .jar file for the corresponding version. <span style="color: red;">Currently only the following versions of JDK are supported, please select the corresponding version. </span>

| java version | download by debug tools                                                                                | [download by github](https://github.com/future0923/debug-tools/releases/tag/dcevm-installer)                                       |
|--------------|--------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| 1.8.0_181    | [DCEVM-8u181-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u181-installer.jar) | [DCEVM-8u181-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u181-installer.jar) |
| 1.8.0_172    | [DCEVM-8u172-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u172-installer.jar) | [DCEVM-8u172-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u172-installer.jar) |
| 1.8.0_152    | [DCEVM-8u152-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u152-installer.jar) | [DCEVM-8u152-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u152-installer.jar) |
| 1.8.0_144    | [DCEVM-8u144-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u144-installer.jar) | [DCEVM-8u144-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u144-installer.jar) |
| 1.8.0_112    | [DCEVM-8u112-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u112-installer.jar) | [DCEVM-8u112-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u112-installer.jar) |
| 1.8.0_92     | [DCEVM-8u92-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u92-installer.jar)   | [DCEVM-8u92-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u92-installer.jar)   |
| 1.8.0_74     | [DCEVM-8u74-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u74-installer.jar)   | [DCEVM-8u74-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u74-installer.jar)   |
| 1.8.0_66     | [DCEVM-8u66-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u66-installer.jar)   | [DCEVM-8u66-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u66-installer.jar)   |
| 1.8.0_51     | [DCEVM-8u51-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u51-installer.jar)   | [DCEVM-8u51-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u51-installer.jar)   |
| 1.8.0_45     | [DCEVM-8u45-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u45-installer.jar)   | [DCEVM-8u45-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u45-installer.jar)   |

Run the corresponding `java -jar DCEVM-8uXX-installer.jar` file, find the corresponding version, and click the `Install DCEVM as altjvm` button.

![dcevm-installer.png](/images/dcevm-installer.png){v-zoom}

#### linux

For example, if you enter `java -XXaltjvm=dcevm -version`, you will get the following prompt

```text
Error: missing `dcevm' JVM at `/home/java/jdk1.8.0_291/jre/lib/amd64/dcevm/libjvm.so'.
Please install or use the JRE or JDK that contains these missing components.
```

Download the corresponding version of the file and rename it to `libjvm.so` and move it to the directory extracted above.

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

Download [trava-jdk-11-dcevm](https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases) for use as jdk11.

### java 17/21

Download [JetBrainsRuntime](https://github.com/JetBrains/JetBrainsRuntime/releases) for use with jdk17/21.

:::tip

If the Apple system prompts that the JDK is damaged or the developer cannot be verified after downloading it, and the JDK cannot be started, enter `sudo xattr -r -d com.apple.quarantine /$jdkPath`, where **$jdkPath** is your JDK directory

:::




