package cn.jaa.mutithread.basic.use;

import cn.jaa.util.Print;

/**
 * @Author: Jaa
 * @Description: 线程的 join（合并） 操作
 * @Date 2024/4/6
 */
public class JoinDemo {

    public static final int SLEEP_GAP = 5000;
    public static final int MAX_TURN = 50;

    static class SleepThread extends Thread {
        static int threadSeqNumber = 1;

        public SleepThread() {
            super("sleepThread-" + threadSeqNumber);
            threadSeqNumber++;
        }

        public void run() {
            try {
                Print.tco(getName() + " 进入睡眠.");
                // 线程睡眠一会
                Thread.sleep(SLEEP_GAP);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Print.tco(getName() + " 发生被异常打断.");
                return;
            }
            Print.tco(getName() + " 运行结束.");
        }

    }


    public static void main(String[] args) {
        Thread thread1 = new SleepThread();
        Print.tco("启动 thread1 线程...");
        thread1.start();

        try {
            thread1.join(); // 合并线程1，不限时
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Print.tco("启动 thread2 线程...");
        // 启动第二条线程，并且进行限时合并，等待时间为1秒
        Thread thread2 = new SleepThread();
        thread2.start();

        try {
            thread2.join(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Print.tco("线程执行结束...");

    }
}
