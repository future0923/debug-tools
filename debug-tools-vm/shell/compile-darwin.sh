#!/bin/bash
COMPILE_JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home
g++ ../src/main/native/src/io_github_future0923_debug_tools_vm_VmTool.cpp -shared -o ../../debug-tools-server/src/main/resources/lib/libJniLibrary.dylib -I$COMPILE_JAVA_HOME/include -I$COMPILE_JAVA_HOME/include/darwin -mmacosx-version-min=14.6.1