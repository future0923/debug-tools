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
package io.github.future0923.debug.tools.test.spring.boot.mybatis.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.validation.Valid;
import java.time.Duration;

/**
 * DebugTools 方法调用页验证 Flux/ServerSentEvent 返回值。
 *
 * @author future0923
 */
@RestController
public class FluxTestController {

    @GetMapping(value = "/debug-tools/flux/string", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stringFlux() {
        return Flux.just("first", "second", "third")
                .delayElements(Duration.ofMillis(300));
    }

    @GetMapping(value = "/debug-tools/flux/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatStreamEvent>> sseFlux() {
        return buildSseFlux("default", "hello flux");
    }

    @PostMapping(value = "/debug-tools/flux/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatStreamEvent>> stream(@Valid @RequestBody ChatRequest request) {
        return buildSseFlux(request.sessionId(), request.message());
    }

    private Flux<ServerSentEvent<ChatStreamEvent>> buildSseFlux(String sessionId, String message) {
        return Flux.range(1, 10)
                .delayElements(Duration.ofMillis(300))
                .map(index -> ServerSentEvent.<ChatStreamEvent>builder()
                        .id(sessionId + "-" + index)
                        .event("chat-message")
                        .comment("debug-tools flux test")
                        .retry(Duration.ofSeconds(3))
                        .data(new ChatStreamEvent(sessionId, index, message + " #" + index))
                        .build());
    }

    public static class ChatStreamEvent {

        private String sessionId;

        private Integer sequence;

        private String content;

        public ChatStreamEvent() {
        }

        public ChatStreamEvent(String sessionId, Integer sequence, String content) {
            this.sessionId = sessionId;
            this.sequence = sequence;
            this.content = content;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public Integer getSequence() {
            return sequence;
        }

        public void setSequence(Integer sequence) {
            this.sequence = sequence;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
