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
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.server.utils.AgentOperationActions;
import io.github.future0923.debug.tools.server.utils.AgentOperationRegistry;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.RejectedExecutionException;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 请求和查询均绑定 agent 实例；拒绝向重启后的 JVM 补发旧操作。 */
public final class AgentOperationsHttpHandler extends BaseHttpHandler<Map<String,Object>,HttpResponse<Object>> {
    public static final String PATH="/operations";
    private final AgentOperationRegistry operations=new AgentOperationRegistry();
    @Override protected HttpResponse<Object> doHandle(Map<String,Object> req,Headers headers) {
        try {
            if(req==null) throw new IllegalArgumentException("Request is required");
            if(!RuntimeDiagnosticsHttpHandler.INSTANCE_ID.equals(req.get("agentInstanceId"))) return HttpResponse.of(409,map("code","TARGET_INSTANCE_CHANGED","message","Expected agentInstanceId does not match"));
            Object id=req.get("operationId");if(!(id instanceof String)) throw new IllegalArgumentException("operationId is required");
            if("GET".equals(req.get("action"))) return HttpResponse.of(200,operations.get((String)id));
            if(!"SUBMIT".equals(req.get("action"))) throw new IllegalArgumentException("Unknown operation action");
            if(!Arrays.asList("GROOVY","RESOURCES","COMPILE","CLASSES").contains(req.get("kind")) || !(req.get("classLoaderIdentity") instanceof String)) throw new IllegalArgumentException("kind and classLoaderIdentity are required");
            String json=DebugToolsJsonUtils.toJsonStr(req);
            if(json.length()>2097152) throw new IllegalArgumentException("Operation payload exceeds 2 MiB");
            String hash=AgentOperationActions.digest(json.getBytes(StandardCharsets.UTF_8));
            return HttpResponse.of(202,operations.submit((String)id,hash,()->AgentOperationActions.execute(req)));
        } catch(IllegalArgumentException error) { return HttpResponse.of(400,map("code","INVALID_ARGUMENT","message",error.getMessage())); }
        catch(RejectedExecutionException error) { return HttpResponse.of(429,map("code","OPERATION_CAPACITY_EXCEEDED","message",error.getMessage())); }
        catch(Exception error) { return HttpResponse.of(500,map("code","OPERATION_FAILED","message",error.toString())); }
    }
    public void close() { operations.close(); }
}
