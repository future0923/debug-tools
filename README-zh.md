[English](README.md) | [中文](README-zh.md)

# DebugTools

- 快速调用Java方法(本地/远程)
- 搜索 http 地址以直接跳转到相应的方法定义。
- 打印SQL语句与耗时
- 执行Groovy脚本

## 文档

要查看文档，请访问 [debug-tools.cc](https://debug-tools.cc)。

## 更新日志

每个版本的详细更改都记录在 [CHANGELOG](https://github.com/future0923/debug-tools/blob/main/CHANGELOG.md) 中。

## 参与贡献

在发出拉取请求之前，请阅读 [贡献指南](https://github.com/future0923/debug-tools/blob/main/.github/contributing.md)。

## 快速开始

### 安装

#### 商店

1. 打开 `IDE Settings` 并选择 `Plugins`
2. 在 `Marketplace` 搜索 `DebugTools` 并点击 `install`
3. 重启应用

![marketplace](/docs/public/images/marketplace.png)

#### 商店地址

```text
https://plugins.jetbrains.com/plugin/24463-debugtools
```

#### 离线下载

```text
https://download.debug-tools.cc/DebugToolsIdeaPlugin.zip
```

#### 手动打包

```sh 
git clone https://github.com/future0923/debug-tools.git
cd debug-tools
mvn clean install -T 2C -Dmaven.test.skip=true
# dist dir
# debug-tools-boot.jar remote agent jar
cd debug-tools-idea
./gradlew clean buildPlugin
# dist dir
# DebugTools-{version}.zip IDEA plugin
```

#### github

```text
https://github.com/future0923/debug-tools/releases
```

#### gitee

```text
https://gitee.com/future94/debug-tools/releases
```

### 启动应用

DebugTools采用`Java Agent`技术实现调试，因此调试的时候必须保证项目已经启动并完成。

### 附着应用

点击Idea右侧的<img src="/docs/public/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" />工具栏，唤醒DebugTools窗口。点击<img src="/docs/public/icon/add.svg" alt="加号" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" />，获取本地可附加的应用程序列表。

![tools_window](/docs/public/images/tools_window.png)

选择您要调试的应用程序，然后单击 `Attach` 按钮以附加该应用程序。

![application_list.png](/docs/public/images/application_list.png)

成功附着应用后，DebugTools 会在显示附着状态。
- `L`: 标识附着的是本地应用，`R`代表是远程应用。
- `Connected`: 应用已经附着成功并连接服务成功。
- `i.g.f.d.t.t.a.DebugToolsTestApplication`: 应用名称。
    - 附着时指定应用名时为`指定的应用名`。
    - 未指定应用名时如果是 Spring 应用取 `spring.application.name` 配置项。
    - 未指定时取启动时jar中的 `Main-Class`。
    - 未找到时取启动命令中的 `sun.java.command`。

![attach_status](/docs/public/images/attach_status.png)

### 调用方法

唤醒要调用的方法上的上下文菜单，点击`Quick Debug`即可唤醒调试面板。

![idea_menu.png](/docs/public/images/idea_menu.png)

如果我们想快速调用`TestService`的`test`方法

```java
package io.github.future0923.debug.tools.test.application.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String test(String name, Integer age) {
        return "name = " + name + ", age = " + age;
    }
}
```

调用方法时输入参数的值

例如`name = DebugTools`，`age = 18`，会自动生成DebugJson格式，这里我们在`content`中传递对应的值就可以了。

![quick_debug](/docs/public/images/quick_debug.png)

单击 `Run` 按钮来调用该方法。

### 展示结果

调用成功后，运行结果（方法的返回值）会显示在DebugTools窗口中。

- **toString**：调用ToString方法后，显示方法的返回值。
- **json**：以Json方式显示方法的返回值。
- **debug**：以Idea Debug类型的样式显示方法返回值。

![run_result](/docs/public/images/run_result.png)
![json_result](/docs/public/images/json_result.png)
![debug_result](/docs/public/images/debug_result.png)