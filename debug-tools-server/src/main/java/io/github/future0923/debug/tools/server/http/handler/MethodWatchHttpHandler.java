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
package io.github.future0923.debug.tools.server.http.handler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.trace.MethodWatchManager;
import java.lang.instrument.Instrumentation;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 真实流量观察独立于主动调用，不发送业务请求。 */
public final class MethodWatchHttpHandler extends BaseHttpHandler<MethodWatchHttpHandler.Request,HttpResponse<Object>> implements AutoCloseable {
    public static final String PATH="/runtime/watch";
    private final MethodWatchManager manager=new MethodWatchManager();
    @Override protected HttpResponse<Object> doHandle(Request req,Headers headers) {
        try {
            if(req==null || !RuntimeDiagnosticsHttpHandler.INSTANCE_ID.equals(req.agentInstanceId))return HttpResponse.of(409,map("code","TARGET_INSTANCE_CHANGED","message","Expected agentInstanceId is required"));
            switch(req.action) {
                case "LIST":return HttpResponse.of(200,map("watches",manager.list()));
                case "READ":return HttpResponse.of(200,manager.read(req.watchId,req.afterCursor,req.limit));
                case "STOP":return HttpResponse.of(200,manager.stop(req.watchId,false));
                case "RELEASE":return HttpResponse.of(200,manager.stop(req.watchId,true));
                case "START":
                    Instrumentation instrumentation=DebugToolsBootstrap.INSTANCE==null?null:DebugToolsBootstrap.INSTANCE.getInstrumentation();
                    if(instrumentation==null)throw new IllegalStateException("Instrumentation unavailable");
                    ClassLoader loader=AllClassLoaderHttpHandler.getClassLoader(req.classLoaderIdentity);
                    Class<?> selected=null;for(Class<?> type:instrumentation.getAllLoadedClasses())if(type.getClassLoader()==loader && type.getName().equals(req.className))selected=type;
                    if(selected==null)throw new IllegalArgumentException("Exact class is not loaded in selected loader");
                    return HttpResponse.of(200,manager.start(instrumentation,selected,req.methodName,req.descriptor,req.maxCalls,req.durationMillis));
                default:throw new IllegalArgumentException("Invalid watch action");
            }
        } catch(IllegalArgumentException error) { return HttpResponse.of(400,map("code","INVALID_ARGUMENT","message",error.getMessage())); }
        catch(Exception | LinkageError error) { return HttpResponse.of(503,map("code","WATCH_UNAVAILABLE","message",error.toString())); }
    }
    @Override public void close() { manager.close(); }
    public static final class Request { public String action="READ",agentInstanceId,watchId,classLoaderIdentity,className,methodName,descriptor;public int maxCalls=20,durationMillis=30000,limit=50;public long afterCursor; }
}
