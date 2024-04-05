package cn.jaa.mutithread.basic.create;

import cn.jaa.util.Print;

import static cn.jaa.util.ThreadUtil.getCurThreadName;

/**
 * @Author: Jaa
 * @Description:
 * @Date 2024/4/5
 */
public class CreateDemo {

    private static final int MAX_TURN = 5;

    // 线程编号
    static int threadNo = 0;

    static class DemoThread extends Thread {

        public DemoThread() {
            super("DemoThread-" + threadNo++);
        }
        public void run() {
            for (int i = 1; i < MAX_TURN; i++) {
                Print.cfo(getName() + ", 轮次: " + i);
            }
            Print.cfo(getName() + " 运行结束.");
        }
    }

    public static void main(String[] args) {
        Thread thread = null;
        // 方法一：使用Thread子类创建和启动线程
        for (int i = 0; i < 2; i++) {
            thread = new DemoThread();
            thread.start();
        }
        Print.cfo(getCurThreadName() +"运行结束.");
    }
}
