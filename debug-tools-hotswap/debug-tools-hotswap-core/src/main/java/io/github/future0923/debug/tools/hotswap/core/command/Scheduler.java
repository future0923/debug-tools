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
package io.github.future0923.debug.tools.hotswap.core.command;

/**
 * 执行{@link Command}的调度器
 */
public interface Scheduler {

    /**
     * 有重复相同任务时如何处理
     */
    enum DuplicateSheduleBehaviour {
        /**
         * 跳过
         */
        SKIP,
        /**
         * 等待运行结束后在运行
         */
        WAIT_AND_RUN_AFTER,
        /**
         * 一起运行
         */
        RUN_DUPLICATE
    }

    /**
     * 投递一个新的命令
     * <p>
     * 如果命令与已经存在的命令equals相同，则会替换并重新计时
     * <p>
     * 该命令会在目标类加载器中运行
     * <p>
     * 默认timeout为100
     * @param command 要执行的命令
     */
    void scheduleCommand(Command command);

    /**
     * 投递一个新的命令
     *
     * @param command 要执行的命令
     * @param timeout 当前时间延迟多久后执行命令（给合并留出时间）
     */
    void scheduleCommand(Command command, int timeout);

    /**
     * 投递一个新的命令
     *
     * @param command 要执行的命令
     * @param timeout 当前时间延迟多久后执行命令（给合并留出时间）
     * @param behaviour 如果有相同正在执行的命令，如何处理
     */
    void scheduleCommand(Command command, int timeout, DuplicateSheduleBehaviour behaviour);

    /**
     * 运行调度器
     */
    void run();

    /**
     * 停止调度器
     */
    void stop();
}
