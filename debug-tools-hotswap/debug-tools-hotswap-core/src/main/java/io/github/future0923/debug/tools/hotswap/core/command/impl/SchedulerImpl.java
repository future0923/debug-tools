/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.hotswap.core.command.impl;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.handler.WatchEventCommand;
import io.github.future0923.debug.tools.hotswap.core.command.Command;
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 调度器默认实现，只调度，命令通过创建{@link CommandExecutor}执行
 */
public class SchedulerImpl implements Scheduler {

    private static final Logger LOGGER = Logger.getLogger(SchedulerImpl.class);

    /**
     * 默认超时时间
     */
    int DEFAULT_SCHEDULING_TIMEOUT = 100;

    /**
     * 投递进来的命令（无序）
     */
    final Map<Command, DuplicateScheduleConfig> scheduledCommands = new ConcurrentHashMap<>();

    /**
     * 正在运行的命令
     */
    final Set<Command> runningCommands = Collections.synchronizedSet(new HashSet<Command>());

    /**
     * 运行命令的调度线程，创建{@link CommandExecutor}最终执行命令
     */
    Thread runner;

    /**
     * 是否停止运行
     */
    boolean stopped;

    @Override
    public void scheduleCommand(Command command) {
        scheduleCommand(command, DEFAULT_SCHEDULING_TIMEOUT);
    }

    @Override
    public void scheduleCommand(Command command, int timeout) {
        scheduleCommand(command, timeout, DuplicateSheduleBehaviour.WAIT_AND_RUN_AFTER);
    }

    @Override
    public void scheduleCommand(Command command, int timeout, DuplicateSheduleBehaviour behaviour) {
        synchronized (scheduledCommands) {
            Command targetCommand = command;
            if (scheduledCommands.containsKey(command) && (command instanceof MergeableCommand)) {
                // 任务中有这个命令并且是可以合并的
                for (Command scheduledCommand : scheduledCommands.keySet()) {
                    // 如果他们的equals相等就合并
                    if (command.equals(scheduledCommand)) {
                        targetCommand = ((MergeableCommand) scheduledCommand).merge(command);
                        break;
                    }
                }
            }

            // map可能已经包含equals命令，put将替换它并重置计时器
            scheduledCommands.put(targetCommand, new DuplicateScheduleConfig(System.currentTimeMillis() + timeout, behaviour));
            LOGGER.trace("{} scheduled for execution in {}ms", targetCommand, timeout);
        }
    }

    /**
     * 运行当前未运行且运行时间{@link DuplicateScheduleConfig#time}
     */
    private boolean processCommands() {
        long currentTime = System.currentTimeMillis();
        synchronized (scheduledCommands) {
            for (Iterator<Map.Entry<Command, DuplicateScheduleConfig>> it = scheduledCommands.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Command, DuplicateScheduleConfig> entry = it.next();
                DuplicateScheduleConfig config = entry.getValue();
                Command command = entry.getKey();
                // 如果时间到了
                if (config.getTime() < currentTime) {
                    // 如果当前正在运行
                    if (runningCommands.contains(command)) {
                        if (config.getBehaviour().equals(DuplicateSheduleBehaviour.SKIP)) {
                            LOGGER.debug("Skipping duplicate running command {}", command);
                            it.remove();
                        } else if (config.getBehaviour().equals(DuplicateSheduleBehaviour.RUN_DUPLICATE)) {
                            executeCommand(command);
                            it.remove();
                        }
                        // 不投递则为等待下次调度
                    } else {
                        executeCommand(command);
                        it.remove();
                    }
                }
            }
        }

        return true;
    }

    /**
     * 创建{@link CommandExecutor}执行命令
     */
    private void executeCommand(Command command) {
        if (command instanceof WatchEventCommand) {
            LOGGER.trace("Executing {}", command);
        } else {
            LOGGER.debug("Executing {}", command);
        }
        //添加到正在运行的集合
        runningCommands.add(command);
        new CommandExecutor(command) {
            @Override
            public void finished() {
                runningCommands.remove(command);
            }
        }.start();
    }

    @Override
    public void run() {
        runner = new Thread(() -> {
            for (; ; ) {
                if (stopped || !processCommands()) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }

        });
        runner.setDaemon(true);
        runner.start();
    }

    @Override
    public void stop() {
        stopped = true;
    }

    @Getter
    private static class DuplicateScheduleConfig {

        /**
         * 什么时间运行
         */
        private final long time;

        /**
         * 有重复命令时如何处理
         */
        DuplicateSheduleBehaviour behaviour;

        private DuplicateScheduleConfig(long time, DuplicateSheduleBehaviour behaviour) {
            this.time = time;
            this.behaviour = behaviour;
        }
    }
}
