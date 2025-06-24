/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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