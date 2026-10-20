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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** 让日志框架在事件创建时冻结关联 ID，兼容 AsyncAppender；退出后恢复原 MDC 值。 */
public final class InvocationLogContext implements AutoCloseable {
    public static final String KEY="debugToolsInvocationId";
    private final List<Runnable> restorers=new ArrayList<>();
    public static InvocationLogContext open(ClassLoader loader,String id) {
        InvocationLogContext scope=new InvocationLogContext();
        if(id==null)return scope;
        for(String name:new String[]{"org.slf4j.MDC","org.apache.logging.log4j.ThreadContext"}) {
            try {
                Class<?> type=Class.forName(name,true,loader);
                Method get=type.getMethod("get",String.class),put=type.getMethod("put",String.class,String.class),remove=type.getMethod("remove",String.class);
                Object previous=get.invoke(null,KEY);put.invoke(null,KEY,id);
                scope.restorers.add(()->{try{if(previous==null)remove.invoke(null,KEY);else put.invoke(null,KEY,previous);}catch(Exception ignored){}});
            } catch(Exception | LinkageError unavailable) { /* 可选日志框架缺失不影响业务调用。 */ }
        }
        return scope;
    }
    @Override public void close() { for(int i=restorers.size()-1;i>=0;i--)restorers.get(i).run();restorers.clear(); }
}
