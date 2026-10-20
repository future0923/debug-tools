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
package io.github.future0923.debug.tools.server.utils;

import io.github.future0923.debug.tools.base.logging.DebugToolsLogBuffer;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 按显式 ClassLoader 安装 root appender；不改变应用日志等级或已有 appender。 */
public final class BusinessLogCapture {
    private static final Map<String, Session> SESSIONS = new LinkedHashMap<>();
    private BusinessLogCapture() { }

    public static synchronized Map<String, Object> start(String loaderId, ClassLoader loader, String framework) throws Exception {
        if (!"LOGBACK".equals(framework) && !"LOG4J2".equals(framework)) throw new IllegalArgumentException("framework must be LOGBACK or LOG4J2");
        String key = loaderId + ":" + framework;
        Session prior = SESSIONS.get(key);
        if (prior != null && Boolean.TRUE.equals(prior.status().get("active"))) return prior.status();
        if (prior != null) { prior.remove.run(); SESSIONS.remove(key); }
        if (SESSIONS.size() >= 32) throw new IllegalStateException("At most 32 log capture sessions may be active");
        Session session = new Session(loaderId, framework);
        if ("LOGBACK".equals(framework)) {
            Object context = loader.loadClass("org.slf4j.LoggerFactory").getMethod("getILoggerFactory").invoke(null);
            Class<?> contextType = loader.loadClass("ch.qos.logback.classic.LoggerContext");
            if (!contextType.isInstance(context)) throw new IllegalStateException("Selected SLF4J binding is not Logback");
            Object root = contextType.getMethod("getLogger", String.class).invoke(context, "ROOT");
            rejectSharedRoot(root);
            session.root = root;
            Class<?> appenderType = loader.loadClass("ch.qos.logback.core.Appender");
            Object appender = session.appender(appenderType, context);
            root.getClass().getMethod("addAppender", appenderType).invoke(root, appender);
            session.remove = () -> root.getClass().getMethod("detachAppender", appenderType).invoke(root, appender);
            session.attached = () -> root.getClass().getMethod("getAppender", String.class).invoke(root, session.name) == appender;
        } else {
            Class<?> manager = loader.loadClass("org.apache.logging.log4j.LogManager");
            Object context = manager.getMethod("getContext", ClassLoader.class, boolean.class).invoke(null, loader, false);
            Class<?> contextType = loader.loadClass("org.apache.logging.log4j.core.LoggerContext");
            if (!contextType.isInstance(context)) throw new IllegalStateException("Selected Log4j context has no core implementation");
            Object config = contextType.getMethod("getConfiguration").invoke(context);
            Object root = loader.loadClass("org.apache.logging.log4j.core.config.Configuration").getMethod("getRootLogger").invoke(config);
            rejectSharedRoot(root);
            session.root = root;
            Class<?> appenderType = loader.loadClass("org.apache.logging.log4j.core.Appender");
            Object appender = session.appender(appenderType, context);
            Class<?> rootType = loader.loadClass("org.apache.logging.log4j.core.config.LoggerConfig");
            rootType.getMethod("addAppender", appenderType, loader.loadClass("org.apache.logging.log4j.Level"), loader.loadClass("org.apache.logging.log4j.core.Filter")).invoke(root, appender, null, null);
            session.remove = () -> {
                rootType.getMethod("removeAppender", String.class).invoke(root, session.name);
                contextType.getMethod("updateLoggers").invoke(context);
            };
            session.attached = () -> {
                Object currentConfig = contextType.getMethod("getConfiguration").invoke(context);
                Object currentRoot = loader.loadClass("org.apache.logging.log4j.core.config.Configuration").getMethod("getRootLogger").invoke(currentConfig);
                return ((Map<?, ?>) rootType.getMethod("getAppenders").invoke(currentRoot)).get(session.name) == appender;
            };
            try { contextType.getMethod("updateLoggers").invoke(context); }
            catch (Exception error) { session.remove.run(); throw error; }
        }
        SESSIONS.put(key, session);
        return session.status();
    }

    public static synchronized Map<String, Object> stop(String loaderId, String framework) throws Exception {
        String key = loaderId + ":" + framework;
        Session session = SESSIONS.get(key);
        if (session == null) return map("active", false, "classLoaderIdentity", loaderId, "framework", framework);
        session.remove.run();
        session.active = false;
        SESSIONS.remove(key);
        return session.status();
    }

    public static synchronized List<Map<String, Object>> status() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Session session : SESSIONS.values()) result.add(session.status());
        return result;
    }

    /** agent 停止时解绑 appender，避免业务日志继续引用已停止的诊断服务。 */
    public static synchronized void closeAll() {
        Iterator<Session> iterator = SESSIONS.values().iterator();
        while (iterator.hasNext()) {
            Session session = iterator.next();
            session.active = false;
            try { session.remove.run(); iterator.remove(); } catch (Exception ignored) { /* 下次关闭仍可重试解绑。 */ }
        }
    }

    private interface Removal { void run() throws Exception; }
    private static void rejectSharedRoot(Object root) {
        for (Session existing : SESSIONS.values()) {
            if (existing.root == root && Boolean.TRUE.equals(existing.status().get("active"))) {
                throw new IllegalStateException("Logging context is already captured via classLoaderIdentity=" + existing.loaderId);
            }
        }
    }
    private static final class Session {
        final String loaderId, framework, name = "DebugToolsCapture-" + UUID.randomUUID();
        final long startedAt = System.currentTimeMillis();
        final AtomicLong captured = new AtomicLong(), failures = new AtomicLong();
        final ThreadLocal<Boolean> capturing = new ThreadLocal<>();
        volatile boolean active = true;
        Removal remove;
        Object root;
        java.util.concurrent.Callable<Boolean> attached;
        Session(String loaderId, String framework) { this.loaderId = loaderId; this.framework = framework; }

        Object appender(Class<?> type, Object context) {
            return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
                switch (method.getName()) {
                    case "doAppend": case "append": if (active && args != null && args.length > 0) capture(args[0]); return null;
                    case "getName": case "toString": return name;
                    case "getContext": return context;
                    case "isStarted": return active;
                    case "start": active = true; return null;
                    case "stop": active = false; return method.getReturnType() == boolean.class ? true : null;
                    case "isStopped": return !active;
                    case "ignoreExceptions": return true;
                    case "getCopyOfAttachedFiltersList": return Collections.emptyList();
                    case "hashCode": return System.identityHashCode(proxy);
                    case "equals": return proxy == args[0];
                    case "getState": return enumValue(method.getReturnType(), active ? "STARTED" : "STOPPED");
                    case "getFilterChainDecision": return enumValue(method.getReturnType(), "NEUTRAL");
                    default: return method.getReturnType() == boolean.class ? false : null;
                }
            });
        }

        void capture(Object event) {
            if (Boolean.TRUE.equals(capturing.get())) return;
            capturing.set(true);
            try {
                String message;
                Object throwable;
                long timestamp;
                if ("LOGBACK".equals(framework)) {
                    message = string(call(event, "getFormattedMessage"));
                    throwable = call(event, "getThrowableProxy");
                    if (throwable != null) throwable = string(call(throwable, "getClassName")) + ": " + string(call(throwable, "getMessage"));
                    timestamp = ((Number) call(event, "getTimeStamp")).longValue();
                } else {
                    message = string(call(call(event, "getMessage"), "getFormattedMessage"));
                    throwable = call(event, "getThrown");
                    timestamp = ((Number) call(event, "getTimeMillis")).longValue();
                }
                Object context = "LOGBACK".equals(framework) ? call(event, "getMDCPropertyMap") : call(call(event, "getContextData"), "toMap");
                Object correlation = context instanceof Map ? ((Map<?,?>)context).get(InvocationLogContext.KEY) : null;
                DebugToolsLogBuffer.add(timestamp, framework, string(call(event, "getLevel")), string(call(event, "getLoggerName")),
                    string(call(event, "getThreadName")), message, throwable == null ? null : string(throwable), correlation == null ? null : String.valueOf(correlation));
                captured.incrementAndGet();
            } catch (Exception | LinkageError error) { failures.incrementAndGet(); }
            finally { capturing.remove(); }
        }

        Map<String, Object> status() {
            Boolean present;
            try { present = attached.call(); } catch (Exception error) { present = null; }
            return map("active", active && Boolean.TRUE.equals(present), "attached", present, "classLoaderIdentity", loaderId, "framework", framework, "startedAt", startedAt,
                "captured", captured.get(), "captureFailures", failures.get(), "scope", "ROOT_APPENDER",
                "limitations", Arrays.asList("Non-additive loggers may not reach the root", "Logger levels/filters still apply", "Capture begins at startedAt", "Reconfiguration can detach the capture appender"));
        }
    }
    private static Object call(Object value, String method) throws Exception { return value.getClass().getMethod(method).invoke(value); }
    private static String string(Object value) { return value == null ? "" : String.valueOf(value); }
    private static Object enumValue(Class<?> type, String name) {
        for (Object value : type.getEnumConstants()) if (((Enum<?>) value).name().equals(name)) return value;
        return null;
    }
}
