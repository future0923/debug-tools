/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.vm;

import java.util.Arrays;

/**
 * @author future0923
 */
public class VmToolTest {

    /**
     * 如果在 cmd 运行，需要先编译成 class 文件，再运行
     * javac -encoding UTF-8 io/github/future0923/debug/tools/vm/VmTool.java
     * java -cp . io.github.future0923.debug.tools.vm.VmTool
     */
    public static void main(String[] args) {
        //String path = "/Users/weilai/Documents/debug-tools/debug-tools-server/src/main/resources/lib/libJniLibrary.dylib";
        String path = "D:\\debug-tools\\debug-tools-server\\src\\main\\resources\\lib\\libJniLibrary-x64.dll";
        VmTool[] instances = VmTool.getInstance(path).getInstances(VmTool.class);
        System.out.println(Arrays.toString(instances));
    }
}