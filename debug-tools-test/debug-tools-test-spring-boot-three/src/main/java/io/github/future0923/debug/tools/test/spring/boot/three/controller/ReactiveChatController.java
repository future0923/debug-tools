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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * @author future0923
 */
@RestController
@RequestMapping("/reactive/chat")
public class ReactiveChatController {

    @GetMapping("/flux")
    public Flux<ChatChunk> flux() {
        return Flux.just(
                new ChatChunk("start", "你好，"),
                new ChatChunk("delta", "我是 DebugTools Reactive 示例。"),
                new ChatChunk("delta", "可以用 $.content 提取并拼接。"),
                new ChatChunk("finish", "")
        ).delayElements(Duration.ofMillis(300));
    }

    @GetMapping("/mono")
    public Mono<ChatChunk> mono() {
        return Mono.just(new ChatChunk("single", "Mono 只会产生一条 NEXT 事件。"));
    }

    public static class ChatChunk {

        private String type;

        private String content;

        public ChatChunk() {
        }

        public ChatChunk(String type, String content) {
            this.type = type;
            this.content = content;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
