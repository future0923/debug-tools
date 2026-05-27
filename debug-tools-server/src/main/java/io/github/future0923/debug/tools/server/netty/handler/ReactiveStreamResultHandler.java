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

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodStreamEventType;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodStreamResponsePacket;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

final class ReactiveStreamResultHandler {

    private static final Logger logger = Logger.getLogger(ReactiveStreamResultHandler.class);

    private static final String REACTIVE_STREAMS_PUBLISHER_CLASS_NAME = "org.reactivestreams.Publisher";

    private static final String REACTIVE_STREAMS_SUBSCRIBER_CLASS_NAME = "org.reactivestreams.Subscriber";

    private final RunTargetMethodResultWriter resultWriter;

    private final Map<String, ActiveStreamSubscription> activeStreamSubscriptions = new ConcurrentHashMap<>();

    ReactiveStreamResultHandler(RunTargetMethodResultWriter resultWriter) {
        this.resultWriter = resultWriter;
    }

    boolean writeIfReactiveStreamResult(Object result, Long duration, RunDTO runDTO, ChannelHandlerContext ctx) {
        if (!isReactiveStreamsPublisher(result)) {
            return false;
        }
        subscribeStreamResult(result, duration, runDTO, ctx);
        return true;
    }

    /**
     * 取消指定 identity 对应的响应式订阅，并向 IDEA 推送取消结束事件。
     */
    boolean cancelStream(String identity) {
        if (DebugToolsStringUtils.isBlank(identity)) {
            return false;
        }
        ActiveStreamSubscription activeSubscription = activeStreamSubscriptions.remove(identity);
        if (activeSubscription == null) {
            return false;
        }
        activeSubscription.cancel();
        return true;
    }

    /**
     * IDEA 连接异常断开时，只取消该连接创建的响应式订阅，避免污染其他正常连接。
     */
    void cancelStreamsByChannel(ChannelHandlerContext ctx) {
        if (ctx == null) {
            return;
        }
        List<ActiveStreamSubscription> subscriptions = new ArrayList<>();
        activeStreamSubscriptions.forEach((identity, subscription) -> {
            if (subscription.belongsTo(ctx)) {
                subscriptions.add(subscription);
            }
        });
        subscriptions.forEach(ActiveStreamSubscription::cancelSilently);
    }

    private boolean isReactiveStreamsPublisher(Object result) {
        if (result == null) {
            return false;
        }
        try {
            Class<?> publisherClass = Class.forName(REACTIVE_STREAMS_PUBLISHER_CLASS_NAME, false, result.getClass().getClassLoader());
            return publisherClass.isInstance(result);
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    /**
     * 订阅响应式返回值，将每个 onNext/onComplete/onError 作为独立 packet 推送给 IDEA。
     */
    private void subscribeStreamResult(Object publisher, Long duration, RunDTO runDTO, ChannelHandlerContext ctx) {
        Class<?> publisherClass;
        Class<?> subscriberClass;
        try {
            ClassLoader classLoader = publisher.getClass().getClassLoader();
            publisherClass = Class.forName(REACTIVE_STREAMS_PUBLISHER_CLASS_NAME, false, classLoader);
            subscriberClass = Class.forName(REACTIVE_STREAMS_SUBSCRIBER_CLASS_NAME, false, classLoader);
        } catch (ClassNotFoundException | LinkageError e) {
            resultWriter.writeNormalResult(publisher, duration, runDTO, ctx, false, false);
            return;
        }
        AtomicLong sequence = new AtomicLong(0);
        AtomicBoolean finished = new AtomicBoolean(false);
        AtomicReference<Object> subscriptionRef = new AtomicReference<>();
        String identity = runDTO.getIdentity();
        Object subscriber = Proxy.newProxyInstance(subscriberClass.getClassLoader(), new Class[]{subscriberClass}, (proxy, method, args) -> {
            String methodName = method.getName();
            if ("onSubscribe".equals(methodName) && args != null && args.length == 1) {
                Object subscription = args[0];
                subscriptionRef.set(subscription);
                if (DebugToolsStringUtils.isNotBlank(identity)) {
                    activeStreamSubscriptions.put(identity, new ActiveStreamSubscription(identity, runDTO, ctx, subscription, sequence, duration, finished));
                }
                requestSubscription(subscription, 1);
                return null;
            }
            if ("onNext".equals(methodName) && args != null && args.length == 1) {
                if (!finished.get()) {
                    Object value = args[0];
                    ctx.writeAndFlush(resultWriter.createNextPacket(runDTO, sequence.incrementAndGet(), value, duration));
                    ActiveStreamSubscription activeSubscription = activeStreamSubscriptions.get(identity);
                    if (activeSubscription != null) {
                        activeSubscription.request(1);
                    } else if (subscriptionRef.get() != null) {
                        requestSubscription(subscriptionRef.get(), 1);
                    }
                }
                return null;
            }
            if ("onError".equals(methodName) && args != null && args.length == 1) {
                if (!finished.compareAndSet(false, true)) {
                    return null;
                }
                removeActiveStreamSubscription(identity);
                RunTargetMethodStreamResponsePacket packet = resultWriter.createStreamPacket(
                        runDTO,
                        sequence.incrementAndGet(),
                        RunTargetMethodStreamEventType.ERROR,
                        duration
                );
                packet.setThrowableMessage((Throwable) args[0]);
                ctx.writeAndFlush(packet);
                return null;
            }
            if ("onComplete".equals(methodName)) {
                if (!finished.compareAndSet(false, true)) {
                    return null;
                }
                removeActiveStreamSubscription(identity);
                ctx.writeAndFlush(resultWriter.createStreamPacket(
                        runDTO,
                        sequence.incrementAndGet(),
                        RunTargetMethodStreamEventType.COMPLETE,
                        duration
                ));
            }
            return null;
        });
        try {
            publisherClass.getMethod("subscribe", subscriberClass).invoke(publisher, subscriber);
        } catch (Exception e) {
            if (!finished.compareAndSet(false, true)) {
                return;
            }
            RunTargetMethodStreamResponsePacket packet = resultWriter.createStreamPacket(
                    runDTO,
                    sequence.incrementAndGet(),
                    RunTargetMethodStreamEventType.ERROR,
                    duration
            );
            packet.setThrowableMessage(e);
            ctx.writeAndFlush(packet);
        }
    }

    private void requestSubscription(Object subscription, long n) {
        try {
            Method method = subscription.getClass().getMethod("request", long.class);
            method.setAccessible(true);
            method.invoke(subscription, n);
        } catch (Exception e) {
            logger.warning("request reactive subscription error", e);
        }
    }

    private void cancelSubscription(Object subscription) {
        try {
            Method method = subscription.getClass().getMethod("cancel");
            method.setAccessible(true);
            method.invoke(subscription);
        } catch (Exception e) {
            logger.warning("cancel reactive subscription error", e);
        }
    }

    private void removeActiveStreamSubscription(String identity) {
        if (DebugToolsStringUtils.isNotBlank(identity)) {
            activeStreamSubscriptions.remove(identity);
        }
    }

    private final class ActiveStreamSubscription {

        private final String identity;

        private final RunDTO runDTO;

        private final ChannelHandlerContext ctx;

        private final Object subscription;

        private final AtomicLong sequence;

        private final Long duration;

        private final AtomicBoolean finished;

        private ActiveStreamSubscription(
                String identity,
                RunDTO runDTO,
                ChannelHandlerContext ctx,
                Object subscription,
                AtomicLong sequence,
                Long duration,
                AtomicBoolean finished
        ) {
            this.identity = identity;
            this.runDTO = runDTO;
            this.ctx = ctx;
            this.subscription = subscription;
            this.sequence = sequence;
            this.duration = duration;
            this.finished = finished;
        }

        private void cancel() {
            cancel(true);
        }

        private void cancelSilently() {
            cancel(false);
        }

        private void cancel(boolean notifyClient) {
            if (!finished.compareAndSet(false, true)) {
                return;
            }
            activeStreamSubscriptions.remove(identity, this);
            cancelSubscription(subscription);
            if (!notifyClient) {
                return;
            }
            ctx.writeAndFlush(resultWriter.createStreamPacket(
                    runDTO,
                    sequence.incrementAndGet(),
                    RunTargetMethodStreamEventType.CANCELLED,
                    duration
            ));
        }

        private boolean belongsTo(ChannelHandlerContext ctx) {
            return this.ctx.channel() == ctx.channel();
        }

        private void request(long n) {
            requestSubscription(subscription, n);
        }
    }
}
