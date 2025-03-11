package io.github.future0923.debug.tools.vm;

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
        System.out.println(instances);
    }
}