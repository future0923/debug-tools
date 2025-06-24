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
package io.github.future0923.debug.tools.hotswap.core.plugin.hotswapper;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将HotSwapperJpda的Class从javassist复制到插件中，如HotSwapperJpda不在应用程序类加载器中tools.jar会NoClassDefFound
 * <p>
 * 通过JPDA(Java Platform Debugger Architecture)动态重新加载类
 * <p>
 * JPDA重新加载的类必须和原来定义的有相同的fields和methods
 * <p>
 * HotSwapperJpda需要启动jvm时增加参数支持，idea启动时的重载用的也是这个
 * <ul>
 * <p>For Java 1.4,<br>
 * <pre>java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000</pre>
 * <p>For Java 5,<br>
 * <pre>java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000</pre>
 * </ul>
 */
public class HotSwapperJpda {

    private final VirtualMachine jvm;

    private final MethodEntryRequest request;

    private Map<ReferenceType, byte[]> newClassFiles;

    private final Trigger trigger;

    private static final String HOST_NAME = "localhost";

    private static final String TRIGGER_NAME = Trigger.class.getName();

    private static final String CONNECTOR_NAME = "com.sun.jdi.SocketAttach";

    /**
     * 连接JVM
     *
     * @param port 连接JVM的端口
     */
    public HotSwapperJpda(int port)
            throws IOException, IllegalConnectorArgumentsException {
        this(Integer.toString(port));
    }

    /**
     * 连接JVM
     *
     * @param port 连接JVM的端口
     */
    public HotSwapperJpda(String port)
            throws IOException, IllegalConnectorArgumentsException {
        newClassFiles = null;
        trigger = new Trigger();
        AttachingConnector connector = (AttachingConnector) findConnector();
        Map<String, Connector.Argument> arguments = connector.defaultArguments();
        arguments.get("hostname").setValue(HOST_NAME);
        arguments.get("port").setValue(port);
        jvm = connector.attach(arguments);
        EventRequestManager manager = jvm.eventRequestManager();
        request = methodEntryRequests(manager);
    }

    private Connector findConnector() throws IOException {
        List<Connector> connectors = Bootstrap.virtualMachineManager().allConnectors();
        for (Connector con : connectors) {
            if (con.name().equals(CONNECTOR_NAME)) {
                return con;
            }
        }
        throw new IOException("Not found: " + CONNECTOR_NAME);
    }

    private static MethodEntryRequest methodEntryRequests(EventRequestManager manager) {
        MethodEntryRequest methodEntryRequest = manager.createMethodEntryRequest();
        methodEntryRequest.addClassFilter(TRIGGER_NAME);
        methodEntryRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        return methodEntryRequest;
    }

    /**
     * 调用{@link #reload}时停止触发
     */
    @SuppressWarnings("unused")
    private void deleteEventRequest(EventRequestManager manager, MethodEntryRequest request) {
        manager.deleteEventRequest(request);
    }

    /**
     * 重载class文件
     *
     * @param className 类全路径
     * @param classFile class文件内容
     */
    public void reload(String className, byte[] classFile) {
        ReferenceType refType = toRefType(className);
        Map<ReferenceType, byte[]> map = new HashMap<>();
        map.put(refType, classFile);
        reload(map, className);
    }

    /**
     * 重载class文件
     */
    public void reload(Map<String, byte[]> classFiles) {
        Map<ReferenceType, byte[]> map = new HashMap<>();
        String className = null;
        for (Map.Entry<String, byte[]> e : classFiles.entrySet()) {
            className = e.getKey();
            map.put(toRefType(className), e.getValue());
        }
        if (className != null) {
            reload(map, className + " etc.");
        }
    }

    /**
     * 获取Class的JDI ReferenceType
     */
    private ReferenceType toRefType(String className) {
        List<ReferenceType> list = jvm.classesByName(className);
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("no such class: " + className);
        }
        return list.get(0);
    }

    /**
     * 启动线程收到MethodEntryEvent时重载Class
     */
    private void reload(Map<ReferenceType, byte[]> map, String msg) {
        synchronized (trigger) {
            startDaemon();
            newClassFiles = map;
            request.enable();
            trigger.doSwap();
            request.disable();
            Map<ReferenceType, byte[]> ncf = newClassFiles;
            if (ncf != null) {
                newClassFiles = null;
                throw new RuntimeException("failed to reload: " + msg);
            }
        }
    }

    private void startDaemon() {
        new Thread() {
            private void errorMsg(Throwable e) {
                System.err.print("Exception in thread \"HotSwap\" ");
                e.printStackTrace(System.err);
            }

            @Override
            public void run() {
                EventSet events = null;
                try {
                    events = waitEvent();
                    EventIterator iter = events.eventIterator();
                    while (iter.hasNext()) {
                        Event event = iter.nextEvent();
                        if (event instanceof MethodEntryEvent) {
                            hotswap();
                            break;
                        }
                    }
                } catch (Throwable e) {
                    errorMsg(e);
                }
                try {
                    if (events != null)
                        events.resume();
                } catch (Throwable e) {
                    errorMsg(e);
                }
            }
        }.start();
    }

    EventSet waitEvent() throws InterruptedException {
        EventQueue queue = jvm.eventQueue();
        return queue.remove();
    }

    /**
     * 调用jvm重载class
     */
    void hotswap() {
        Map<ReferenceType, byte[]> map = newClassFiles;
        jvm.redefineClasses(map);
        newClassFiles = null;
    }

}
