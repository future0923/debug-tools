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
package io.github.future0923.debug.tools.idea.client.socket;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.client.config.ClientConfig;
import io.github.future0923.debug.tools.client.netty.dispatcher.ClientNettyPacketDispatcher;
import io.github.future0923.debug.tools.client.netty.handler.ClientDispatchHandler;
import io.github.future0923.debug.tools.common.codec.PacketFrameDecoder;
import io.github.future0923.debug.tools.common.codec.PacketNettyDecoder;
import io.github.future0923.debug.tools.common.codec.PacketNettyEncoder;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DebugToolsNettyTcpClient {

    private static final Logger logger = Logger.getLogger(DebugToolsNettyTcpClient.class);

    private final ClientConfig config;
    private final ClientNettyPacketDispatcher dispatcher;

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private volatile Channel channel;

    private final AtomicBoolean started = new AtomicBoolean(false);
    private volatile long reconnectDelayMillis;

    @Getter
    private ClientConnectState state = ClientConnectState.INIT;

    public DebugToolsNettyTcpClient(ClientConfig config, ClientNettyPacketDispatcher dispatcher) {
        this.config = Objects.requireNonNull(config);
        this.dispatcher = Objects.requireNonNull(dispatcher);
        this.reconnectDelayMillis = config.getReconnectInitialDelayMillis();
        start();
    }

    public void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }
        state = ClientConnectState.INIT;
        group = new MultiThreadIoEventLoopGroup(1, r -> {
            Thread t = new Thread(r);
            t.setName("DebugTools-Netty-Client-IO");
            t.setDaemon(true);
            return t;
        }, NioIoHandler.newFactory());
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channelFactory(NioSocketChannel::new)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        // 1) 粘包/半包：按协议 length 切帧
                        p.addLast("frame", PacketFrameDecoder.newDefault());
                        // 2) 解码/编码：Packet <-> ByteBuf
                        p.addLast("decoder", new PacketNettyDecoder());
                        p.addLast("encoder", new PacketNettyEncoder());
                        // 3) 心跳：写空闲触发
                        p.addLast("idle", new IdleStateHandler(
                                0,
                                Math.max(1, config.getHeartbeatSeconds()),
                                0,
                                TimeUnit.SECONDS
                        ));
                        // 4) 收包分发
                        p.addLast("dispatch", new ClientDispatchHandler(dispatcher));
                    }
                });
    }

    public void connect(Runnable onConnectFailed) {
        if (!started.get()) {
            return;
        }
        logger.info("Netty client connect to {}:{}", config.getHost(), config.getPort());
        // 标记状态
        state = (state == ClientConnectState.RECONNECTING)
                ? ClientConnectState.RECONNECTING
                : ClientConnectState.CONNECTING;
        bootstrap.connect(config.getHost(), config.getPort()).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                state = ClientConnectState.CONNECT_FAILED;
                if (onConnectFailed != null) {
                    onConnectFailed.run();
                }
                return;
            }
            channel = future.channel();
            state = ClientConnectState.CONNECTED;
            reconnectDelayMillis = config.getReconnectInitialDelayMillis();
            logger.info("Netty client connected: {}", channel);
            channel.closeFuture().addListener((ChannelFutureListener) cf -> {
                logger.warning("Netty client channel closed: {}", channel);
                channel = null;
                if (started.get()) {
                    state = ClientConnectState.RECONNECTING;
                    scheduleReconnect();
                }
            });
        });
    }

    private void scheduleReconnect() {
        if (!started.get()) {
            return;
        }
        if (!config.isAutoReconnect()) {
            return;
        }

        long delay = Math.min(reconnectDelayMillis, config.getReconnectMaxDelayMillis());
        reconnectDelayMillis = Math.min(reconnectDelayMillis * 2, config.getReconnectMaxDelayMillis());

        logger.info("Netty client reconnect in {} ms", delay);

        group.schedule(() -> connect(null), delay, TimeUnit.MILLISECONDS);
    }

    public boolean isActive() {
        Channel ch = this.channel;
        return ch != null && ch.isActive();
    }

    public void send(Packet packet) {
        Channel ch = this.channel;
        if (ch == null || !ch.isActive()) {
            throw new IllegalStateException("Netty client not connected");
        }
        ch.writeAndFlush(packet);
    }

    public void stop() {
        if (!started.compareAndSet(true, false)) {
            return;
        }
        state = ClientConnectState.CLOSED;
        try {
            Channel ch = this.channel;
            if (ch != null) {
                ch.close();
                this.channel = null;
            }
        } catch (Exception ignored) {
        }

        try {
            if (group != null) {
                group.shutdownGracefully();
            }
        } catch (Exception ignored) {
        }

        logger.info("Netty client stopped");
    }
}
