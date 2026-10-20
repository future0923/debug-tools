#!/usr/bin/env bash
set -euo pipefail

# 从脚本位置定位源码，支持在仓库根目录或 shell 目录执行。
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_DIR="$(cd -- "$SCRIPT_DIR/../.." && pwd)"
COMPILE_JAVA_HOME="${COMPILE_JAVA_HOME:-${JAVA_HOME:-}}"
CXX="${CXX:-g++}"

if [[ "$(uname -s)" != Linux ]]; then
    echo "错误: 请在 Linux 上编译 Linux JNI 库。" >&2
    exit 1
fi
if [[ -z "$COMPILE_JAVA_HOME" || ! -f "$COMPILE_JAVA_HOME/include/jni.h" || ! -f "$COMPILE_JAVA_HOME/include/linux/jni_md.h" ]]; then
    echo "错误: 请设置 JAVA_HOME 或 COMPILE_JAVA_HOME，指向包含 Linux JNI 头文件的 JDK。" >&2
    exit 1
fi
if ! command -v "$CXX" >/dev/null 2>&1; then
    echo "错误: 未找到 C++ 编译器 $CXX，请先安装 g++ 和静态 C++ 运行库。" >&2
    exit 1
fi

# 根据编译器目标选择文件名，交叉编译时也不会错误覆盖另一架构的库。
TARGET_TRIPLE="$("$CXX" -dumpmachine)"
case "$TARGET_TRIPLE" in
    aarch64*-linux-* | arm64*-linux-*) LIB_NAME=libJniLibrary-arm64.so ;;
    x86_64*-linux-*) LIB_NAME=libJniLibrary-x64.so ;;
    *) echo "错误: 不支持的 Linux 编译目标 $TARGET_TRIPLE" >&2; exit 1 ;;
esac

OUTPUT_DIR="${OUTPUT_DIR:-$REPO_DIR/debug-tools-server/src/main/resources/lib}"
mkdir -p "$OUTPUT_DIR"
"$CXX" "$REPO_DIR/debug-tools-vm/src/main/native/src/io_github_future0923_debug_tools_vm_VmTool.cpp" \
    -std=c++11 -O2 -shared -fPIC -Wl,-z,defs \
    -I"$COMPILE_JAVA_HOME/include" -I"$COMPILE_JAVA_HOME/include/linux" \
    -static-libgcc -static-libstdc++ -o "$OUTPUT_DIR/$LIB_NAME"
echo "已生成: $OUTPUT_DIR/$LIB_NAME ($TARGET_TRIPLE)"
