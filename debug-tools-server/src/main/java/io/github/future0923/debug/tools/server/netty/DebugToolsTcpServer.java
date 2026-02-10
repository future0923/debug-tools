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
package io.github.future0923.debug.tools.server.netty;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.codec.PacketFrameDecoder;
import io.github.future0923.debug.tools.common.codec.PacketNettyDecoder;
import io.github.future0923.debug.tools.common.codec.PacketNettyEncoder;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.netty.dispatcher.ServerDispatcherFactory;
import io.github.future0923.debug.tools.server.netty.dispatcher.ServerPacketDispatcher;
import io.github.future0923.debug.tools.server.netty.dispatcher.ServerDispatchHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * tcp server
 *
 * @author future0923
 */
public final class DebugToolsTcpServer {

    private static final Logger logger = Logger.getLogger(DebugToolsTcpServer.class);

    /**
     * 业务线程池：所有耗时逻辑只允许在这里跑
     */
    private final ExecutorService bizPool;

    /**
     * boss线程
     */
    private EventLoopGroup boss;

    /**
     * worker线程
     */
    private EventLoopGroup worker;

    /**
     * 服务端通道
     */
    private Channel serverChannel;

    public DebugToolsTcpServer() {
        int cores = Math.max(2, Runtime.getRuntime().availableProcessors());
        this.bizPool = new ThreadPoolExecutor(
                cores,
                Math.max(cores, cores * 4),
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(12),
                r -> {
                    Thread t = new Thread(r);
                    t.setName("DebugTools-BizPool-" + t.getId());
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public void start() throws InterruptedException {
        boss = new NioEventLoopGroup(1, r -> {
            Thread t = new Thread(r);
            t.setName("DebugTools-Netty-Boss");
            t.setDaemon(true);
            return t;
        });
        worker = new NioEventLoopGroup(0, r -> {
            Thread t = new Thread(r);
            t.setName("DebugTools-Netty-Worker-" + t.getId());
            t.setDaemon(true);
            return t;
        });
        ServerPacketDispatcher dispatcher = ServerDispatcherFactory.create();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 64 * 1024))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("idle", new IdleStateHandler(120, 0, 0));
                        p.addLast("frame", PacketFrameDecoder.newDefault());
                        p.addLast("decoder", new PacketNettyDecoder());
                        p.addLast("encoder", new PacketNettyEncoder());
                        p.addLast("dispatch", new ServerDispatchHandler(bizPool, dispatcher));
                    }
                });
        int bindPort = DebugToolsBootstrap.serverConfig.getTcpPort();
        serverChannel = bootstrap.bind(bindPort).sync().channel();
        logger.info("Netty TCP server started, bind port {}", bindPort);
    }

    public void stop() {
        try {
            if (serverChannel != null) serverChannel.close();
        } catch (Exception ignored) {
        }

        try {
            if (boss != null) boss.shutdownGracefully();
        } catch (Exception ignored) {
        }

        try {
            if (worker != null) worker.shutdownGracefully();
        } catch (Exception ignored) {
        }

        bizPool.shutdown();
        logger.info("Netty TCP server stopped");
    }
}
