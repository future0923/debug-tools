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
package io.github.future0923.debug.tools.test.spring.boot.three.controller;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * @author future0923
 */
@RestController
@RequestMapping("/reactive/sse")
public class ReactiveSseController {

    @GetMapping("/agent")
    public Flux<ServerSentEvent<ReactiveChatController.ChatChunk>> agent() {
        return Flux.just(
                ServerSentEvent.builder(new ReactiveChatController.ChatChunk("delta", "第一段 SSE 内容，"))
                        .id("1")
                        .event("agent_delta")
                        .comment("DebugTools Reactive SSE demo")
                        .build(),
                ServerSentEvent.builder(new ReactiveChatController.ChatChunk("delta", "第二段 SSE 内容。"))
                        .id("2")
                        .event("agent_delta")
                        .build(),
                ServerSentEvent.builder(new ReactiveChatController.ChatChunk("done", ""))
                        .id("3")
                        .event("agent_done")
                        .build()
        ).delayElements(Duration.ofMillis(300));
    }
}
