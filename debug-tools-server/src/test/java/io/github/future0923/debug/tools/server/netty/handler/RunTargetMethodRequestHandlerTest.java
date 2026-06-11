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
package io.github.future0923.debug.tools.server.netty.handler;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RunTargetMethodRequestHandlerTest {

    @Test
    void handlerBytecodeDoesNotHardReferenceReactiveStreams() throws Exception {
        String resourceName = RunTargetMethodRequestHandler.class.getSimpleName() + ".class";
        try (InputStream inputStream = RunTargetMethodRequestHandler.class.getResourceAsStream(resourceName)) {
            assertTrue(inputStream != null);
            String classBytes = new String(inputStream.readAllBytes(), StandardCharsets.ISO_8859_1);
            assertFalse(classBytes.contains("org/reactivestreams"));
        }
    }

    @Test
    void cancelStreamsByChannelOnlyCancelsSubscriptionsOwnedByThatChannel() throws Exception {
        ReactiveStreamResultHandler handler = new ReactiveStreamResultHandler(new RunTargetMethodResultWriter());
        EmbeddedChannel firstChannel = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        EmbeddedChannel secondChannel = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        RecordingSubscription firstSubscription = new RecordingSubscription();
        RecordingSubscription secondSubscription = new RecordingSubscription();

        subscribeForTest(handler, "stream-first", firstChannel.pipeline().firstContext(), firstSubscription);
        subscribeForTest(handler, "stream-second", secondChannel.pipeline().firstContext(), secondSubscription);

        handler.cancelStreamsByChannel(firstChannel.pipeline().firstContext());

        assertTrue(firstSubscription.cancelled.get());
        assertFalse(secondSubscription.cancelled.get());
    }

    @Test
    void setRequestIsNoOpWhenSpringWebIsAbsent() throws Exception {
        String classPath = Arrays.stream(System.getProperty("java.class.path").split(System.getProperty("path.separator")))
                .filter(path -> !path.contains("spring-web-"))
                .collect(Collectors.joining(System.getProperty("path.separator")));
        Process process = new ProcessBuilder(
                System.getProperty("java.home") + "/bin/java",
                "-cp",
                classPath,
                SetRequestWithoutSpringWeb.class.getName()
        ).redirectErrorStream(true).start();

        int exitCode = process.waitFor();

        assertTrue(exitCode == 0, new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
    }

    private static void subscribeForTest(
            ReactiveStreamResultHandler handler,
            String identity,
            ChannelHandlerContext ctx,
            RecordingSubscription subscription
    ) {
        io.github.future0923.debug.tools.common.dto.RunDTO runDTO = new io.github.future0923.debug.tools.common.dto.RunDTO();
        runDTO.setIdentity(identity);
        handler.writeIfReactiveStreamResult((Publisher<Object>) subscriber -> subscriber.onSubscribe(subscription), 0L, runDTO, ctx);
    }

    private static class RecordingSubscription implements Subscription {

        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        @Override
        public void request(long n) {
        }

        @Override
        public void cancel() {
            cancelled.set(true);
        }
    }

    public static class SetRequestWithoutSpringWeb {

        public static void main(String[] args) {
            io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils.setRequest(new io.github.future0923.debug.tools.common.dto.RunDTO());
        }
    }
}
