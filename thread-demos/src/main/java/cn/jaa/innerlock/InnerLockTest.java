package cn.jaa.innerlock;

import cn.jaa.util.Print;
import org.junit.Test;
import org.openjdk.jol.vm.VM;

import java.util.concurrent.CountDownLatch;

import static cn.jaa.util.ThreadUtil.sleepMilliSeconds;

/**
 * @Author: Jaa
 * @Description: 对象布局分析的用例代码
 * @Date 2024/4/9
 */
public class InnerLockTest {

    final int MAX_TREAD = 10;
    final int MAX_TURN = 1000;
    CountDownLatch latch = new CountDownLatch(MAX_TREAD);

    /**
     * 测试无锁对象
     */
    @Test
    public void showNoLockObject() throws InterruptedException {
        // 输出JVM的信息
        Print.fo(VM.current().details());
        // 创建一个对象
        ObjectLock objectLock = new ObjectLock();
        Print.fo("object status: ");
        // 输出对象的布局信息
        objectLock.printSelf();
    }

    /**
     * 偏向锁案例演示
     */
    @Test
    public void showBiasedLock() throws InterruptedException {
        Print.tcfo(VM.current().details());
        // JVM延迟偏向锁
        sleepMilliSeconds(5000);

        ObjectLock lock = new ObjectLock();
        Print.tcfo("抢占锁前, lock 的状态: ");
        lock.printObjectStruct();

        sleepMilliSeconds(5000);
        CountDownLatch latch = new CountDownLatch(1);

        Runnable runnable = () -> {
            for (int i = 0; i < MAX_TURN; i++) {
                synchronized (lock) {
                    lock.increase();
                    if (i == MAX_TURN / 2) {
                        Print.tcfo("占有锁, lock 的状态: ");
                        lock.printObjectStruct();
                        // 读取字符串型输入,阻塞线程
                        // Print.consoleInput();
                    }
                }
                // 每一次循环等待10ms
                sleepMilliSeconds(10);
            }
            latch.countDown();
        };
        new Thread(runnable, "biased-demo-thread").start();
        // 等待加锁线程执行完成
        latch.await();
        Print.tcfo("释放锁后, lock 的状态: ");
        lock.printObjectStruct();
    }

    /**
     * 轻量级锁案例演示
     */
    @Test
    public void showLightweightLock() throws InterruptedException {
        Print.tcfo(VM.current().details());
        // JVM延迟偏向锁
        sleepMilliSeconds(5000);

        ObjectLock lock = new ObjectLock();

        Print.tcfo("抢占锁前, lock 的状态: ");
        lock.printObjectStruct();

        sleepMilliSeconds(5000);
        CountDownLatch latch = new CountDownLatch(2);
        Runnable runnable = () -> {
            for (int i = 0; i < MAX_TURN; i++) {
                synchronized (lock) {
                    lock.increase();
                    if (i == 1) {
                        Print.tcfo("第一个线程占有锁, lock 的状态: ");
                        lock.printObjectStruct();
                    }
                }
            }
            //循环完毕
            latch.countDown();

            // 线程虽然释放锁，但是一直存在
            for (int j = 0; ; j++) {
                // 每一次循环等待1ms
                sleepMilliSeconds(1);
            }
        };
        new Thread(runnable).start();

        sleepMilliSeconds(1000); //等待1s

        Runnable lightweightRunnable = () -> {
            for (int i = 0; i < MAX_TURN; i++) {
                synchronized (lock) {
                    lock.increase();
                    if (i == MAX_TURN / 2) {
                        Print.tcfo("第二个线程占有锁, lock 的状态: ");
                        lock.printObjectStruct();
                    }
                    // 每一次循环等待1ms
                    sleepMilliSeconds(1);
                }
            }
            // 循环完毕
            latch.countDown();
        };
        new Thread(lightweightRunnable).start();
        // 等待加锁线程执行完成
        latch.await();
        sleepMilliSeconds(2000);  // 等待2s
        Print.tcfo("释放锁后, lock 的状态: ");
        lock.printObjectStruct();
    }
}
