package cn.jaa.mutithread.basic.create3;

import cn.jaa.util.Print;

import java.util.ArrayList;
import java.util.List;

import static cn.jaa.util.ThreadUtil.getCurThread;
import static cn.jaa.util.ThreadUtil.sleepMilliSeconds;
import static cn.jaa.util.ThreadUtil.sleepSeconds;

/**
 * @Author: Jaa
 * @Description: 线程状态示例
 * @Date 2024/4/6
 */
public class StatusDemo {

    // 每个线程执行的轮次
    public static final int MAX_TURN = 5;
    // 线程编号
    public static int threadNo = 0;
    // 全局静态线程列表
    static List<Thread> threadList = new ArrayList<>();

    /**
     * 输出静态线程列表中每个线程的状态
     */
    private static void printThreadStatus() {
        for (Thread thread : threadList) {
            Print.tco(thread.getName() + " 状态为 " + thread.getState());
        }
    }

    /**
     * 向全局的静态线程列表加入线程
     *
     * @param thread
     */
    private static void addStatusThread(Thread thread) {
        threadList.add(thread);
    }

    static class StatusDemoThread extends Thread {
        public StatusDemoThread() {
            super("statusPrintThread" + (++threadNo));
            // 将自己加入到全局的静态线程列表
            addStatusThread(this);
        }

        public void run() {
            Print.cfo(getName() + ", 状态为 " + getState());
            for (int i = 0; i < MAX_TURN; i++) {
                // 线程睡眠
                sleepMilliSeconds(500);
                // 输出所有线程的状态
                printThreadStatus();
            }
            Print.tco(getName() + "- 运行结束...");
        }
    }

    public static void main(String[] args) {
        // 将 main线程 加入到全局列表
        addStatusThread(Thread.currentThread());

        // 新建三个线程，这些线程在构造器中会将自己加入到全局列表
        Thread sThread1 = new StatusDemoThread();
        Print.cfo(sThread1.getName() + "- 状态为" + sThread1.getState());
        Thread sThread2 = new StatusDemoThread();
        Print.cfo(sThread2.getName() + "- 状态为" + sThread2.getState());
        Thread sThread3 = new StatusDemoThread();
        Print.cfo(sThread3.getName() + "- 状态为" + sThread3.getState());

        // 启动第一个线程
        sThread1.start();

        // 等待500ms后启动第二个线程
        sleepMilliSeconds(500);
        sThread2.start();

        // 等待500ms后启动第三个线程
        sleepMilliSeconds(500);
        sThread3.start();

        // 睡眠100s
        sleepSeconds(100);
    }


}
