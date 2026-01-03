package io.github.future0923.debug.tools.test.simple;

public class DaemonExample {
    public static void main(String[] args) throws InterruptedException {
        Thread daemonThread = new Thread(() -> {
            while (true) {
                System.out.println("守护线程正在运行...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "daemon-thread");
//        daemonThread.setDaemon(true); // 设置为守护线程，必须在start()之前
        daemonThread.start();
        daemonThread.join();
        // 主线程（用户线程）休眠2秒后结束
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程执行完毕，即将退出。");
    }
}
