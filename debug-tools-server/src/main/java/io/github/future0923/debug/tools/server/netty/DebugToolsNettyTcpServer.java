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
import io.github.future0923.debug.tools.server.netty.dispatcher.ServerNettyDispatcherFactory;
import io.github.future0923.debug.tools.server.netty.dispatcher.ServerNettyPacketDispatcher;
import io.github.future0923.debug.tools.server.netty.handler.ServerDispatchHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 【最终版】DebugTools Netty TCP Server
 * - 只支持 Netty
 * - 无 BIO 痕迹
 * - IO / 业务彻底隔离
 */
public final class DebugToolsNettyTcpServer {

    private static final Logger logger = Logger.getLogger(DebugToolsNettyTcpServer.class);

    /**
     * 业务线程池：所有耗时逻辑只允许在这里跑
     */
    private final ExecutorService bizPool;

    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private Channel serverChannel;

    public DebugToolsNettyTcpServer() {

        int cores = Math.max(2, Runtime.getRuntime().availableProcessors());
        this.bizPool = new ThreadPoolExecutor(
                cores,
                Math.max(cores, cores * 4),
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50_000),
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

        // ✅ 最终版：Netty 原生分发器
        ServerNettyPacketDispatcher dispatcher = ServerNettyDispatcherFactory.create();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        // 半包 / 粘包
                        p.addLast("frame", PacketFrameDecoder.newDefault());
                        // 解码 / 编码（零拷贝）
                        p.addLast("decoder", new PacketNettyDecoder());
                        p.addLast("encoder", new PacketNettyEncoder());
                        // IO 分发 + 业务池 + Idle 探测
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
