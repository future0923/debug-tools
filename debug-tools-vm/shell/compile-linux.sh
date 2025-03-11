#!/bin/bash
yum -y install gcc-c++ libstdc++-static
COMPILE_JAVA_HOME=$JAVA_HOME
if [ -z "$COMPILE_JAVA_HOME" ]; then
    echo "错误: JAVA_HOME 未设置！请运行 'export JAVA_HOME=/path/to/java' 或在 /etc/profile 配置 JAVA_HOME。" >&2
    exit 1
fi
g++ ../src/main/native/src/io_github_future0923_debug_tools_vm_VmTool.cpp -shared -o ../../debug-tools-server/src/main/resources/lib/libJniLibrary-x64.so -fPIC -I$COMPILE_JAVA_HOME/include -I$COMPILE_JAVA_HOME/include/linux -static-libgcc -static-libstdc++