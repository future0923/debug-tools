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
package io.github.future0923.debug.tools.base.utils;

import java.util.concurrent.TimeUnit;

/**
 * @author future0923
 */
public class DebugToolsThreadUtils {

    /**
     * 挂起当前线程
     *
     * @param timeout  挂起的时长
     * @param timeUnit 时长单位
     * @return 被中断返回false，否则true
     */
    public static boolean sleep(Number timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout.longValue());
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    /**
     * 挂起当前线程
     *
     * @param millis 挂起的毫秒数
     * @return 被中断返回false，否则true
     */
    public static boolean sleep(Number millis) {
        if (millis == null) {
            return true;
        }
        return sleep(millis.longValue());
    }

    /**
     * 挂起当前线程
     *
     * @param millis 挂起的毫秒数
     * @return 被中断返回false，否则true
     * @since 5.3.2
     */
    public static boolean sleep(long millis) {
        if (millis > 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 考虑{@link Thread#sleep(long)}方法有可能时间不足给定毫秒数，此方法保证sleep时间不小于给定的毫秒数
     *
     * @param millis 给定的sleep时间
     * @return 被中断返回false，否则true
     */
    public static boolean safeSleep(Number millis) {
        if (millis == null) {
            return true;
        }

        return safeSleep(millis.longValue());
    }

    /**
     * 考虑{@link Thread#sleep(long)}方法有可能时间不足给定毫秒数，此方法保证sleep时间不小于给定的毫秒数
     *
     * @param millis 给定的sleep时间
     * @return 被中断返回false，否则true
     */
    public static boolean safeSleep(long millis) {
        long done = 0;
        long before;
        long spendTime;
        while (done >= 0 && done < millis) {
            before = System.currentTimeMillis();
            if (!sleep(millis - done)) {
                return false;
            }
            spendTime = System.currentTimeMillis() - before;
            if (spendTime <= 0) {
                // Sleep花费时间为0或者负数，说明系统时间被拨动
                break;
            }
            done += spendTime;
        }
        return true;
    }
}
