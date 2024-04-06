package cn.jaa.mutithread.basic.use;

import cn.jaa.util.Print;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.jaa.util.ThreadUtil.sleepSeconds;

/**
 * @Author: Jaa
 * @Description: 线程的 Yield（让步）操作
 * @Date 2024/4/6
 *
 * 线程调用yield之后，操作系统在重新进行线程调度时偏向于将执行机会让给优先级较高的线程
 *
 * Thread.yeid()方法有以下特点：
 * （1）yield仅能使一个线程从运行状态转到就绪状态，而不是阻塞状态。
 * （2）yield不能保证使得当前正在运行的线程迅速转换到就绪状态。
 * （3）即使完成了迅速切换，系统通过线程调度机制从所有就绪线程中挑选下一个执行线程时，
 *     就绪的线程有可能被选中，也有可能不被选中，其调度的过程受到其他因素（如优先级）的影响。
 */
public class YieldDemo {

    // 执行次数
    public static final int MAX_TURN = 100;
    // 执行编号
    public static AtomicInteger index = new AtomicInteger(0);

    // 记录线程的执行次数
    private static Map<String, AtomicInteger> metric = new HashMap<>();

    private static void printMetric() {
        Print.tco("metric = " + metric);
    }

    static class YieldThread extends Thread {
        static int threadSeqNumber = 1;

        public YieldThread() {
            super("YieldThread-" + threadSeqNumber);
            threadSeqNumber++;
            metric.put(this.getName(), new AtomicInteger(0));
        }

        @Override
        public void run() {
            for (int i = 1; i < MAX_TURN && index.get() < MAX_TURN; i++) {
                Print.tco("线程优先级：" + getPriority());
                index.incrementAndGet();
                // 统计一次
                metric.get(this.getName()).incrementAndGet();
                if (i % 2 == 0) {
                    //让步：出让执行的权限
                    Thread.yield();
                }
                // 输出所有线程的执行次数
                printMetric();
                Print.tco(getName() + "运行结束...");
            }
        }
    }

    @Test
    public void test() {
        Thread thread1 = new YieldThread();
        thread1.setPriority(Thread.MAX_PRIORITY);

        Thread thread2 = new YieldThread();
        thread2.setPriority(Thread.MIN_PRIORITY);

        Print.tco("启动线程.");
        thread1.start();
        thread2.start();
        sleepSeconds(100);
    }
}
