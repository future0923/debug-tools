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

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringConfigHttpHandlerTest {

    @Test
    void loadSpringConfigSkipsBlankKeys() {
        Map<String, Object> result = SpringConfigHttpHandler.loadSpringConfig(new String[]{"", "   "});

        assertTrue(result.isEmpty());
    }

    @Test
    void loadSpringConfigKeepsRequestedMissingKey() {
        Map<String, Object> result = SpringConfigHttpHandler.loadSpringConfig(new String[]{"debug.tools.missing.config"});

        assertTrue(result.containsKey("debug.tools.missing.config"));
    }

    @Test
    void loadSpringConfigHandlesNullRequestAsEmptyMap() {
        Map<String, Object> result = SpringConfigHttpHandler.loadSpringConfig(null);

        assertTrue(result.isEmpty());
        assertFalse(result.containsKey(""));
    }

    @Test
    void handleAcceptsJsonArrayRequestBody() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.createContext(SpringConfigHttpHandler.PATH, SpringConfigHttpHandler.INSTANCE);
        server.start();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    "http://127.0.0.1:" + server.getAddress().getPort() + SpringConfigHttpHandler.PATH
            ).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            byte[] requestBody = "[\"debug.tools.missing.config\"]".getBytes(StandardCharsets.UTF_8);
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody);
            }

            assertEquals(200, connection.getResponseCode());
            String responseBody = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(responseBody.contains("debug.tools.missing.config"));
        } finally {
            server.stop(0);
        }
    }
}
