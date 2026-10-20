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
package io.github.future0923.debug.tools.base.trace;

import java.util.concurrent.Callable;

/** 调用关联上下文；显式包装异步任务时按提交时快照传播，结束后恢复线程旧值。 */
public final class InvocationContext {
    private static final ThreadLocal<String> ID=new ThreadLocal<>();
    private InvocationContext() { }
    public static String currentId() { return ID.get(); }
    public static Scope open(String id) { String previous=ID.get();if(id==null)ID.remove();else ID.set(id);return new Scope(previous); }
    public static Runnable wrap(Runnable task) { String captured=ID.get();return ()->{try(Scope ignored=open(captured)){task.run();}}; }
    public static <T> Callable<T> wrap(Callable<T> task) { String captured=ID.get();return ()->{try(Scope ignored=open(captured)){return task.call();}}; }
    public static final class Scope implements AutoCloseable {
        private final String previous;private boolean closed;
        private Scope(String previous) { this.previous=previous; }
        @Override public void close() { if(closed)return;closed=true;if(previous==null)ID.remove();else ID.set(previous); }
    }
}
