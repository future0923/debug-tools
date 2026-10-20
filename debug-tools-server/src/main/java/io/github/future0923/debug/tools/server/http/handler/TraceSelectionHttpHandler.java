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
import io.github.future0923.debug.tools.server.trace.TraceMethodClassFileTransformer;
import java.util.*;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 维护原有 trace 选择；明确其按类名作用于全部匹配 ClassLoader 的语义。 */
public final class TraceSelectionHttpHandler extends BaseHttpHandler<TraceSelectionHttpHandler.Request,HttpResponse<Object>> {
    public static final String PATH="/trace/selection";
    public static final TraceSelectionHttpHandler INSTANCE=new TraceSelectionHttpHandler();
    @Override protected HttpResponse<Object> doHandle(Request req,Headers headers) {
        try {
            if(req==null)req=new Request();
            if(req.offset<0 || req.offset>100000 || req.limit<1 || req.limit>200)throw new IllegalArgumentException("Invalid trace page");
            if(!"LIST".equals(req.action)) {
                if(!RuntimeDiagnosticsHttpHandler.INSTANCE_ID.equals(req.agentInstanceId))return HttpResponse.of(409,map("code","TARGET_INSTANCE_CHANGED","message","Expected agentInstanceId is required for mutations"));
                if(!"ALL_CLASSLOADERS".equals(req.scope))throw new IllegalArgumentException("Explicit scope ALL_CLASSLOADERS is required");
                if(req.className==null || req.className.length()>512 || req.methodName==null || req.methodName.length()>256 || req.descriptor==null || req.descriptor.length()>2048 || !req.descriptor.startsWith("("))throw new IllegalArgumentException("Exact class/method/JVM descriptor is required");
                if("ADD".equals(req.action))TraceMethodClassFileTransformer.traceMethod(req.className,req.methodName,req.descriptor);
                else if("REMOVE".equals(req.action))TraceMethodClassFileTransformer.cancelTraceMethod(req.className,req.methodName,req.descriptor);
                else throw new IllegalArgumentException("Invalid trace action");
            }
            List<String> selections=TraceMethodClassFileTransformer.selectedTraceMethods();
            return HttpResponse.of(200,map("scope","ALL_CLASSLOADERS","registeredMethods",selections.subList(Math.min(req.offset,selections.size()),Math.min(selections.size(),req.offset+req.limit)),
                "total",selections.size(),"action",req.action,"acknowledged",true,"acknowledgementScope","TRANSFORMER_REGISTRATION_OR_RESET","businessExecutionObserved",false));
        } catch(IllegalArgumentException error) { return HttpResponse.of(400,map("code","INVALID_ARGUMENT","message",error.getMessage())); }
        catch(Exception | LinkageError error) { return HttpResponse.of(503,map("code","TRACE_CHANGE_FAILED","message",error.toString())); }
    }
    public static final class Request { public String action="LIST",agentInstanceId,scope,className,methodName,descriptor;public int offset,limit=50; }
}
