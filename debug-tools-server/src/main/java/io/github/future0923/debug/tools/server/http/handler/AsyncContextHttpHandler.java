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
import io.github.future0923.debug.tools.server.trace.AsyncContextManager;
import java.lang.instrument.Instrumentation;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 实例绑定的异步关联管理，START 仅改变明确选定的应用提交点。 */
public final class AsyncContextHttpHandler extends BaseHttpHandler<AsyncContextHttpHandler.Request,HttpResponse<Object>> implements AutoCloseable {
    public static final String PATH="/runtime/async-context";
    private final AsyncContextManager manager=new AsyncContextManager();
    @Override protected HttpResponse<Object> doHandle(Request req,Headers headers){
        try {
            if(req==null || !RuntimeDiagnosticsHttpHandler.INSTANCE_ID.equals(req.agentInstanceId))return HttpResponse.of(409,map("code","TARGET_INSTANCE_CHANGED","message","Expected agent instance is required"));
            if("LIST".equals(req.action))return HttpResponse.of(200,map("contexts",manager.list()));
            if("STOP".equals(req.action))return HttpResponse.of(200,manager.stop(req.contextId));
            if(!"START".equals(req.action))throw new IllegalArgumentException("Invalid action");
            Instrumentation instrumentation=DebugToolsBootstrap.INSTANCE==null?null:DebugToolsBootstrap.INSTANCE.getInstrumentation();
            if(instrumentation==null)throw new IllegalStateException("Instrumentation unavailable");
            ClassLoader loader=AllClassLoaderHttpHandler.getClassLoader(req.classLoaderIdentity);
            for(Class<?> type:instrumentation.getAllLoadedClasses())if(type.getClassLoader()==loader && type.getName().equals(req.className))return HttpResponse.of(200,manager.start(instrumentation,type));
            throw new IllegalArgumentException("Exact executor class is not loaded");
        }catch(IllegalArgumentException error){return HttpResponse.of(400,map("code","INVALID_ARGUMENT","message",error.getMessage()));}
        catch(Exception | LinkageError error){return HttpResponse.of(503,map("code","ASYNC_CONTEXT_UNAVAILABLE","message",error.toString()));}
    }
    @Override public void close(){manager.close();}
    public static final class Request{public String action="LIST",agentInstanceId,contextId,className,classLoaderIdentity;}
}
