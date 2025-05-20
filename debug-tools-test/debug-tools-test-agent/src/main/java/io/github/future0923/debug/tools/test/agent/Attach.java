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
package io.github.future0923.debug.tools.test.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author future0923
 */
public class Attach {

    public static void main(String[] args) throws Exception {
        Set<VirtualMachineDescriptor> collect = VirtualMachine.list().stream().filter(descriptor -> descriptor.displayName().startsWith("io.github.future0923.debug.tools.test.spring.boot.mybatis.SpringBootMybatis")).collect(Collectors.toSet());
        for (VirtualMachineDescriptor descriptor : collect) {
            String id = descriptor.id();
            VirtualMachine attach = VirtualMachine.attach(id);
            attach.loadAgent("/Users/weilai/Documents/debug-tools/debug-tools-test/debug-tools-test-agent/target/agent.jar");
        }
    }
}
