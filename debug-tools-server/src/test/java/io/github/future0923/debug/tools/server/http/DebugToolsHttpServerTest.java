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
package io.github.future0923.debug.tools.server.http;

import org.junit.jupiter.api.Test;
import java.net.HttpURLConnection;
import java.net.URL;
import static org.junit.jupiter.api.Assertions.*;

/** 使用临时端口验证真实 HTTP 生命周期，避免手工调试 sleep 阻塞完整测试。 */
class DebugToolsHttpServerTest {
    @Test void servesCapabilitiesAndClosesAfterRequest() throws Exception {
        DebugToolsHttpServer server=new DebugToolsHttpServer(0);
        server.start();
        HttpURLConnection connection=(HttpURLConnection)new URL("http://127.0.0.1:"+server.getPort()+"/capabilities").openConnection();
        connection.setConnectTimeout(2000);connection.setReadTimeout(2000);
        connection.setRequestMethod("POST");connection.setDoOutput(true);
        try {
            try(java.io.OutputStream request=connection.getOutputStream()){request.write("{}".getBytes(java.nio.charset.StandardCharsets.UTF_8));}
            assertTrue(server.getPort()>0);
            assertEquals(200,connection.getResponseCode());
            java.io.ByteArrayOutputStream output=new java.io.ByteArrayOutputStream();
            try(java.io.InputStream input=connection.getInputStream()){byte[] buffer=new byte[1024];int n;while((n=input.read(buffer))!=-1)output.write(buffer,0,n);}
            String body=new String(output.toByteArray(),java.nio.charset.StandardCharsets.UTF_8);
            assertTrue(body.contains("agentInstanceId"));assertTrue(body.contains("invocation.targetBinding"));
        } finally {connection.disconnect();server.close();}
    }
}
