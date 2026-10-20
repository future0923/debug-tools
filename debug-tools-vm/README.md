
- 运行 `gen-h.sh` 可以根据java文件生成对应的 c++ 头文件.
- 运行 `compile-xx.sh` 可以编译对应的文件到 `debug-tools-server/src/main/resources/lib` 目录下.

### mac

运行 `compile-darwin.sh` 编译成对应文件.

### linux

在 Linux 上安装 JDK、g++ 和静态 C++ 运行库，然后从任意目录执行：

```bash
export JAVA_HOME=/path/to/linux-jdk
bash debug-tools-vm/shell/compile-linux.sh
```

脚本按编译器目标架构输出到 `debug-tools-server/src/main/resources/lib`：

- x86_64：`libJniLibrary-x64.so`
- aarch64 / arm64：`libJniLibrary-arm64.so`

可使用 `CXX` 指定编译器、`COMPILE_JAVA_HOME` 指定 Linux JDK 头文件目录、`OUTPUT_DIR` 指定输出目录。
脚本不会自动安装系统软件包；例如 Debian/Ubuntu 安装 `g++`，CentOS 安装 `gcc-c++ libstdc++-static`。

GitHub Actions 的 `Linux aarch64 JNI` 工作流使用原生 ARM64 runner，在
`manylinux2014_aarch64` 容器内以 glibc 2.17 为基线编译，并验证 ELF 架构、glibc 版本和 SHA-256。
同一产物会在 ARM64 的 JDK 8、17、21、25 上验证加载、堆实例查询和旧架构缓存迁移。
工作流支持手动触发，也会在相关源码推送或 PR 时运行。构建产物名为 `linux-aarch64-jni`。

下载并确认整次工作流通过后，先验证产物，再将 `.so` 放回资源目录：

```bash
gh run download RUN_ID -R future0923/DebugTools -n linux-aarch64-jni -D /tmp/debug-tools-native
(cd /tmp/debug-tools-native && shasum -a 256 -c SHA256SUMS)
cp /tmp/debug-tools-native/libJniLibrary-arm64.so debug-tools-server/src/main/resources/lib/
```

该产物面向 glibc Linux，不包含 Alpine/musl 支持。重新打包 agent 后才会携带更新后的原生库。

### windows

1. 安装[cygwin](https://cygwin.com/install.html)管理工具
2. 选择安装 `mingw64-x86_64-gcc-g++` 下一步
3. 添加 `C:\cygwin64\bin` 到环境变量
4. 运行 `compile-window.bat` 编译成对应文件.
