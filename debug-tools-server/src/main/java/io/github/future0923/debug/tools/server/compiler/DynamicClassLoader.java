package io.github.future0923.debug.tools.server.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态编译类加载器
 *
 * @author future0923
 */
public class DynamicClassLoader extends ClassLoader {

    private final Map<String, MemoryByteCode> byteCodes = new HashMap<>();

    public DynamicClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    /**
     * 注册对应的编译资源文件
     */
    public void registerCompiledSource(MemoryByteCode byteCode) {
        byteCodes.put(byteCode.getClassName(), byteCode);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        MemoryByteCode byteCode = byteCodes.get(name);
        if (byteCode == null) {
            return super.findClass(name);
        }
        return super.defineClass(name, byteCode.getByteCode(), 0, byteCode.getByteCode().length);
    }

    /**
     * 获取编译信息的class对象
     */
    public Map<String, Class<?>> getClasses() throws ClassNotFoundException {
        Map<String, Class<?>> classes = new HashMap<>();
        for (MemoryByteCode byteCode : byteCodes.values()) {
            classes.put(byteCode.getClassName(), findClass(byteCode.getClassName()));
        }
        return classes;
    }

    /**
     * 获取编译信息的class字节码
     */
    public Map<String, byte[]> getByteCodes() {
        Map<String, byte[]> result = new HashMap<>(byteCodes.size());
        for (Map.Entry<String, MemoryByteCode> entry : byteCodes.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getByteCode());
        }
        return result;
    }
}