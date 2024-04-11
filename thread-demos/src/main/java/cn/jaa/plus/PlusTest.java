package cn.jaa.plus;

import cn.jaa.util.Print;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: Jaa
 * @Description:
 * @Date 2024/4/8
 */
public class PlusTest {

    final int MAX_TREAD = 10;
    final int MAX_TURN = 1000;
    // 倒数闩，需要倒数MAX_TREAD次
    CountDownLatch latch = new CountDownLatch(MAX_TREAD);

    /**
     * 测试用例：测试不安全的累加器
     * <p>
     * 为什么自增运算符不是线程安全的呢？
     * 实际上，一个自增运算符是一个复合操作，至少包括三个JVM指令：“内存取值”“寄存器增加1”和“存值到内存”。
     * 这三个指令在JVM内部是独立进行的，中间完全可能会出现多个线程并发进行。
     * 比如在amount=100时，假设有三个线程同一时间读取amount值，读到的都是100，增加1后结果为101，
     * 三个线程都将结果存入amount的内存，amount的结果是101，而不是103。
     * <p>
     * “内存取值”“寄存器增加1”和“存值到内存”这三个JVM指令本身是不可再分的，它们都具备原子性，是线程安全的，也叫原子操作。
     * 但是，两个或者两个以上的原子操作合在一起进行操作就不再具备原子性了。
     * 比如先读后写，就有可能在读之后，其实这个变量被修改了，出现读和写数据不一致的情况。
     */
    @Test
    public void testNotSafePlus() throws InterruptedException {
        NotSafePlus counter = new NotSafePlus();
        Runnable runnable = () -> {
            for (int i = 0; i < MAX_TURN; i++) {
                counter.selfPlus();
            }
            latch.countDown(); // 倒数闩减少一次
        };
        for (int i = 0; i < MAX_TREAD; i++) {
            new Thread(runnable).start();
        }
        latch.await(); // 等待倒数闩的次数减少到0，所有的线程执行完成
        Print.tcfo("理论结果：" + MAX_TURN * MAX_TREAD);
        Print.tcfo("实际结果：" + counter.getAmount());
        Print.tcfo("差距是：" + (MAX_TURN * MAX_TREAD - counter.getAmount()));

        // [main|PlusTest.testNotSafePlus]：理论结果：10000
        // [main|PlusTest.testNotSafePlus]：实际结果：2442
        // [main|PlusTest.testNotSafePlus]：差距是：7558
    }

    /**
     * 测试用例：安全的累加器
     */
    @Test
    public void testSafePlus() throws InterruptedException {
        SafePlus counter = new SafePlus();
        Runnable runnable = () -> {
            for (int i = 0; i < MAX_TURN; i++) {
                counter.selfPlus();
            }
            latch.countDown();
        };
        for (int i = 0; i < MAX_TREAD; i++) {
            new Thread(runnable).start();
        }
        latch.await();
        Print.tcfo("理论结果：" + MAX_TURN * MAX_TREAD);
        Print.tcfo("实际结果：" + counter.getAmount());
        Print.tcfo("差距是：" + (MAX_TURN * MAX_TREAD - counter.getAmount()));
    }
}
