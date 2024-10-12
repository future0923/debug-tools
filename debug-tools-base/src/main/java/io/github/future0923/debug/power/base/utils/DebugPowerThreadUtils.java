package io.github.future0923.debug.power.base.utils;

import java.util.concurrent.TimeUnit;

/**
 * @author future0923
 */
public class DebugPowerThreadUtils {

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
