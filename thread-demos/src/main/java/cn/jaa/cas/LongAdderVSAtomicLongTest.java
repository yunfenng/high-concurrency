package cn.jaa.cas;

import cn.jaa.util.Print;
import cn.jaa.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Author: Jaa
 * @Description: LongAdder 和 AtomicLong 的对比实验
 * @Date 2024/4/13
 * 
 * 通过对比实验可以看到：
 * 当有10个线程总计累加10?000次的时候，AtomicLong的性能更好。
 * 随着累加次数的增加，CAS操作的次数急剧增多，AtomicLong的性能急剧下降。
 * 从对比实验的结果可以看出，在CAS争用最为激烈的场景下，LongAdder的性能是AtomicLong的8倍。
 */
public class LongAdderVSAtomicLongTest {

    // 每条线程的执行轮数
    final int TURNS = 1000000000;

    /**
     * 使用 AtomicLong 完成 10个线程 每个线程累加1000次
     */
    @Test
    public void testAtomicLong() {
        // 并发任务数
        final int TASK_AMOUNT = 10;
        // 线程池，获取CPU密集型任务线程池
        ThreadPoolExecutor pool = ThreadUtil.getCpuIntenseTargetThreadPool();
        // 定义一个原子对象
        AtomicLong atomicLong = new AtomicLong(0);
        // 线程同步倒数闩
        CountDownLatch latch = new CountDownLatch(TASK_AMOUNT);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < TASK_AMOUNT; i++) {
            // 提交任务
            pool.submit(() -> {
                try {
                    for (int j = 0; j < TURNS; j++) {
                        // 执行累加操作
                        atomicLong.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 线程执行完毕，倒数闩减一
                latch.countDown();
            });
        }

        try {
            // 等待所有线程执行完毕，倒数闩完成所有的倒数操作
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        float time = (System.currentTimeMillis() - startTime) / 1000F;
        // 输出统计结果
        Print.tcfo("运行的时长为：" + time);
        Print.tcfo("累加结果为：" + atomicLong.get());

        // [main|LongAdderVSAtomicLongTest.testAtomicLong]：运行的时长为：17.283
        // [main|LongAdderVSAtomicLongTest.testAtomicLong]：累加结果为：1000000000
    }

    /**
     * 使用 LongAdder 完成 10个线程累加1000万次
     */
    @Test
    public void testLongAdder() {
        // 并发任务数
        final int TASK_AMOUNT = 10;
        // 线程池，获取CPU密集型任务线程池
        ThreadPoolExecutor pool = ThreadUtil.getCpuIntenseTargetThreadPool();
        // 定义一个 LongAdder 对象
        LongAdder longAdder = new LongAdder();
        // 线程同步倒数闩
        CountDownLatch latch = new CountDownLatch(TASK_AMOUNT);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < TASK_AMOUNT; i++) {
            // 提交任务
            pool.submit(() -> {
                try {
                    for (int j = 0; j < TURNS; j++) {
                        // 执行累加操作
                        longAdder.add(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 线程执行完毕，倒数闩减一
                latch.countDown();
            });
        }
        // 等待所有线程执行完毕，倒数闩完成所有的倒数操作
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        float time = (System.currentTimeMillis() - startTime) / 1000F;
        // 输出统计结果
        Print.tcfo("运行的时长为：" + time);
        Print.tcfo("累加结果为：" + longAdder.longValue());

        // [main|LongAdderVSAtomicLongTest.testLongAdder]：运行的时长为：1.191
        // [main|LongAdderVSAtomicLongTest.testLongAdder]：累加结果为：1000000000
    }
}
