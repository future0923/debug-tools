#!/bin/bash
COMPILE_JAVA_HOME=$JAVA_HOME
g++ ../src/main/native/src/io_github_future0923_debug_tools_vm_VmTool.cpp -shared -o ../../debug-tools-server/src/main/resources/lib/libJniLibrary-x64.so -fPIC -I$COMPILE_JAVA_HOME/include -I$COMPILE_JAVA_HOME/include/linux -static-libgcc -static-libstdc++