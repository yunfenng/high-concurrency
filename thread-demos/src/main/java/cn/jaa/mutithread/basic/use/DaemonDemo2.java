package cn.jaa.mutithread.basic.use;

import cn.jaa.util.Print;

import static cn.jaa.util.ThreadUtil.getCurThreadName;
import static cn.jaa.util.ThreadUtil.sleepMilliSeconds;

/**
 * @Author: Jaa
 * @Description: 守护线程创建的线程也是守护线程
 * @Date 2024/4/7
 */
public class DaemonDemo2 {

    // 每一轮的睡眠时长
    public static final int SLEEP_GAP = 500;
    // 线程执行轮次
    public static final int MAX_TURN = 5;

    static class NormalThread extends Thread {
        static int threadNo = 1;
        public NormalThread() {
            super("normalThread-" + threadNo);
            threadNo++;
        }
        public void run() {
            while (true) {
                sleepMilliSeconds(SLEEP_GAP);
                Print.synTco(getName() + ", 守护状态为： " + isDaemon());
            }
        }
    }
    public static void main(String[] args) {
        Thread daemonThread = new Thread(() -> {
            for (int i = 0; i < MAX_TURN; i++) {
                NormalThread normalThread = new NormalThread();
                // normalThread.setDaemon(false);
                normalThread.start();
            }
        }, "daemonThread");
        daemonThread.setDaemon(true);
        daemonThread.start();
        // 这里，一定不能让main线程立即结束，否则看不到结果
        sleepMilliSeconds(SLEEP_GAP);
        Print.synTco(getCurThreadName() + " 运行结束.");
    }
}
