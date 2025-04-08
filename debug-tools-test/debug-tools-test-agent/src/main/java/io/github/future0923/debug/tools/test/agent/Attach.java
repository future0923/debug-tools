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
