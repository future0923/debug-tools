package io.github.future0923.debug.tools.vm;

import java.util.Map;

/**
 * @author future0923
 */
public class VmTool {

    /**
     * 不要修改jni-lib的名称
     */
    public final static String JNI_LIBRARY_NAME = "libJniLibrary";

    private static VmTool instance;

    private VmTool() {
    }

    public static VmTool getInstance() {
        return getInstance(null);
    }

    public static synchronized VmTool getInstance(String libPath) {
        if (instance != null) {
            return instance;
        }

        if (libPath == null) {
            System.loadLibrary(JNI_LIBRARY_NAME);
        } else {
            System.load(libPath);
        }

        instance = new VmTool();
        return instance;
    }

    /**
     * 强制jvm进行gc
     */
    private static synchronized native void forceGc0();

    /**
     * 获取某个class在jvm中当前所有存活实例
     */
    private static synchronized native <T> T[] getInstances0(Class<T> klass, int limit);

    /**
     * 统计某个class在jvm中当前所有存活实例的总占用内存，单位：Byte
     */
    private static synchronized native long sumInstanceSize0(Class<?> klass);

    /**
     * 获取某个实例的占用内存，单位：Byte
     */
    private static native long getInstanceSize0(Object instance);

    /**
     * 统计某个class在jvm中当前所有存活实例的总个数
     */
    private static synchronized native long countInstances0(Class<?> klass);

    /**
     * 获取所有已加载的类
     */
    private static synchronized native Class<?>[] getAllLoadedClasses0(Class<?> klass);

    /**
     * <a href="https://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html#ForceGarbageCollection">ForceGarbageCollection</a>
     */
    public void forceGc() {
        forceGc0();
    }

    /**
     * 打断指定线程
     *
     * @param threadId 线程ID
     */
    public void interruptSpecialThread(int threadId) {
        Map<Thread, StackTraceElement[]> allThread = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : allThread.entrySet()) {
            if (entry.getKey().getId() == threadId) {
                entry.getKey().interrupt();
                return;
            }
        }
    }

    /**
     * 获取某个class在jvm中当前所有存活实例
     */
    public <T> T[] getInstances(Class<T> klass) {
        return getInstances0(klass, -1);
    }

    /**
     * 获取某个class在jvm中当前所有存活实例
     *
     * @param limit 如果小于 0 ，则不限制
     */
    public <T> T[] getInstances(Class<T> klass, int limit) {
        if (limit == 0) {
            throw new IllegalArgumentException("limit can not be 0");
        }
        return getInstances0(klass, limit);
    }

    /**
     * 统计某个class在jvm中当前所有存活实例的总占用内存，单位：Byte
     */
    public long sumInstanceSize(Class<?> klass) {
        return sumInstanceSize0(klass);
    }

    /**
     * 获取某个实例的占用内存，单位：Byte
     */
    public long getInstanceSize(Object instance) {
        return getInstanceSize0(instance);
    }

    /**
     * 统计某个class在jvm中当前所有存活实例的总个数
     */
    public long countInstances(Class<?> klass) {
        return countInstances0(klass);
    }

    /**
     * 获取所有已加载的类
     */
    public Class<?>[] getAllLoadedClasses() {
        return getAllLoadedClasses0(Class.class);
    }
}
