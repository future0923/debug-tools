# Java多版本管理

Java包管理有很多，比如: [SDKMAN](https://sdkman.io/) 、[jEnv](https://github.com/jenv/jenv)、[jabba](https://github.com/shyiko/jabba)等。

我们这里`需要添加现有的 JDK`，所以这里使用 **jEnv** 来管理java多版本。其他的可以自己了解一下。

## 1. 安装 jEnv

```shell
# 手动源码安装 
# 不要用其他安装，其他的不带插件或者极简版本会少东西
rm -rf .jenv
git clone https://github.com/jenv/jenv.git ~/.jenv
echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.zshrc
echo 'eval "$(jenv init -)"' >> ~/.zshrc
source ~/.zshrc
# 重要，可以切换 JAVA_HOME 等环境变量
jenv enable-plugin export
exec "$SHELL"
```
## 2. 添加JDK

```shell
mkdir -p ~/.jenv/versions/
````

### jenv add <Badge type="warning" text="不推荐" />

使用 `jenv add` 添加，会自动创建一堆乱七八糟的版本

```shell
# 默认添加
jenv add /Library/Java/JavaVirtualMachines/jbr_jcef-17.0.14-osx-x64-b1367.22/Contents/Home
```

默认会切割为多个版本，都能识别这个jdk

```text
jetbrains64-17.0.14 added
17.0.14 added
17.0 added
17 added
```

### ln -s <Badge type="warning" text="推荐" />

通过`ln`自己创建软连接来生成指定版本号

```shell
# 这样就会创建17的版本
ln -s /Library/Java/JavaVirtualMachines/jbr_jcef-17.0.14-osx-x64-b1367.22/Contents/Home ~/.jenv/versions/17
# 这样就会创建jbr-17.0.14的版本
ln -s /Library/Java/JavaVirtualMachines/jbr_jcef-17.0.14-osx-x64-b1367.22/Contents/Home ~/.jenv/versions/jbr-17.0.14
```

### 我的版本

```shell
ln -s /Library/Java/JavaVirtualMachines/jbr_jcef-21.0.6-osx-x64-b631.42/Contents/Home ~/.jenv/versions/21
ln -s /Library/Java/JavaVirtualMachines/jbr_jcef-17.0.14-osx-x64-b1367.22/Contents/Home ~/.jenv/versions/17
ln -s /Library/Java/JavaVirtualMachines/jbr_dcevm-11.0.15-b2043.56/Contents/Home ~/.jenv/versions/11 
ln -s /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home ~/.jenv/versions/8
```

刷新

```shell
jenv rehash
```

## 3. 查看版本

```shell
jenv versions
```

```text
➜  ~ jenv versions
* system (set by /Users/weilai/.jenv/version)
  11
  17
  21
  8
```

## 4. 删除指定版本

```shell
rm -rf ~/.jenv/versions/[上面的版本]
# eg:rm -rf ~/.jenv/versions/21
jenv rehash
```

## 5. 切换版本

使用 `jenv [global|local|shell] (version)` 切换 Java 版本

```shell
# 全局切换（影响整个系统用户环境）
# 设置后，你所有 shell 中默认都是这个版本。
jenv global 8
# 本地切换（只影响某个目录）
# 会在当前目录下生成一个 .java-version 文件，进入这个目录时自动生效。
jenv local 8
# 临时切换（只影响当前 shell 会话）
# 只在当前命令行窗口生效，关闭就失效。
jenv shell 8

# 验证
java -version
which java
echo $JAVA_HOME
```