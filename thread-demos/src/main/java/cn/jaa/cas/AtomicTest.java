package cn.jaa.cas;

import cn.jaa.util.Print;
import cn.jaa.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Jaa
 * @Description: atomic 测试
 * @Date 2024/4/12
 */
public class AtomicTest {

    private static final int THREAD_COUNT = 10;

    /**
     * 基础原子类 AtomicInteger 的使用示例
     */
    @Test
    public void atomicIntegerTest() {
        int tempValue = 0;
        // 定义一个整数原子类实例，赋值到变量 i
        AtomicInteger i = new AtomicInteger(0);

        // 取值，然后设置一个新值
        tempValue = i.getAndSet(3);
        Print.fo("tempValue:" + tempValue + ";  i:" + i.get()); // tempValue:0;  i:3

        //取值，然后自增
        tempValue = i.getAndIncrement();
        //输出
        Print.fo("tempValue:" + tempValue + ";  i:" + i.get()); // tempValue:3;  i:4

        // 取值，然后增加5
        tempValue = i.getAndAdd(5);
        Print.fo("tempValue:" + tempValue + ";  i:" + i.get()); // tempValue:4;  i:9

        // CAS交换
        boolean flag = i.compareAndSet(9, 100);
        // 输出
        Print.fo("flag:" + flag + ";  i:" + i.get()); // flag:true;  i:100
    }

    // 10个线程自增10000
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < THREAD_COUNT; i++) {
            // 创建10个线程,模拟多线程环境
            ThreadUtil.getMixedTargetThreadPool().submit(() -> {
                for (int j = 0; j < 1000; j++) {
                    atomicInteger.getAndIncrement();
                }
                latch.countDown();
            });
        }
        latch.await();
        Print.tco("累加之和: " + atomicInteger.get());
    }
}
