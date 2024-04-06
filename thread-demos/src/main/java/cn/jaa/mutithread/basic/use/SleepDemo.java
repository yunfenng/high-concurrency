package cn.jaa.mutithread.basic.use;

import cn.jaa.util.Print;

import static cn.jaa.util.ThreadUtil.getCurThreadName;

/**
 * @Author: Jaa
 * @Description: 线程的 sleep 操作
 * @Date 2024/4/6
 */
public class SleepDemo {
    public static final int SLEEP_GAP = 5000;
    public static final int MAX_TURN = 50;

    static class SleepThread extends Thread {
        static int threadSeqNumber = 1;

        public SleepThread() {
            super("sleepThread-" + threadSeqNumber++);
        }

        public void run() {
            for (int i = 0; i < MAX_TURN; i++) {
                try {
                    Print.tco(getName() + ", 睡眠轮次：" + i);
                    // 线程睡眠一会
                    Thread.sleep(SLEEP_GAP);
                } catch (InterruptedException e) {
                    Print.tco(getName() + " 发生异常被中断.");
                }
                Print.tco(getName() + " 运行结束.");
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            Thread thread = new SleepThread();
            thread.start();
        }
        Print.tco(getCurThreadName() + " 运行结束.");
    }
}
