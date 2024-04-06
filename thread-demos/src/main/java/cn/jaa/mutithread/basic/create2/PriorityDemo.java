package cn.jaa.mutithread.basic.create2;

import cn.jaa.util.Print;

/**
 * @Author: Jaa
 * @Description: 线程的优先级
 * @Date 2024/4/6
 *
 * [PriorityDemo.main]：thread-1; 优先级-1; 机会值-677264763
 * [PriorityDemo.main]：thread-2; 优先级-2; 机会值-680783024
 * [PriorityDemo.main]：thread-3; 优先级-3; 机会值-728387318
 * [PriorityDemo.main]：thread-4; 优先级-4; 机会值-686807181
 * [PriorityDemo.main]：thread-5; 优先级-5; 机会值-737638588
 * [PriorityDemo.main]：thread-6; 优先级-6; 机会值-720800697
 * [PriorityDemo.main]：thread-7; 优先级-7; 机会值-754345546
 * [PriorityDemo.main]：thread-8; 优先级-8; 机会值-757580565
 * [PriorityDemo.main]：thread-9; 优先级-9; 机会值-758555513
 * [PriorityDemo.main]：thread-10; 优先级-10; 机会值-753229341
 *
 * （1）整体而言，高优先级的线程获得的执行机会更多。
 *      从实例中可以看到：优先级在6级以上的线程和4级以下的线程执行机会明显偏多，整体对比非常明显。
 * （2）执行机会的获取具有随机性，优先级高的不一定获得的机会多。
 *      比如，例子中的thread-10比thread-9优先级高，但是thread-10所获得的机会反而偏少。
 *
 * 线程优先级越高，CPU分配到的时间片就越多，优先级越高的线程越有可能先执行。
 */
public class PriorityDemo {

    public static final int SLEEP_GAP = 1000;

    static class PrioritySetThread extends Thread {
        static int threadNo = 1;

        public PrioritySetThread() {
            super("thread-" + threadNo);
            threadNo++;
        }

        public long opportunities = 0;

        @Override
        public void run() {
            for (int i = 0; ; i++) {
                opportunities++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PrioritySetThread[] threads = new PrioritySetThread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new PrioritySetThread();
            // 优先级设置 1~10
            threads[i].setPriority(i + 1);
        }
        for (int i = 0; i < threads.length; i++) {
            // 启动线程
            threads[i].start();
        }

        Thread.sleep(SLEEP_GAP);
        for (int i = 0; i < threads.length; i++) {
            // 停止线程
            threads[i].stop();
        }
        for (int i = 0; i < threads.length; i++) {
            Print.cfo(threads[i].getName() + "; 优先级-" + threads[i].getPriority() +"; 机会值-" + threads[i].opportunities);
        }
    }
}
