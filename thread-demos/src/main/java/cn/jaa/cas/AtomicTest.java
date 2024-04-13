package cn.jaa.cas;

import cn.jaa.im.common.bean.User;
import cn.jaa.util.Print;
import cn.jaa.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

import static cn.jaa.util.ThreadUtil.sleepMilliSeconds;

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

    /**
     * 数组原子类 AtomicIntegerArray 的使用示例
     */
    @Test
    public void testAtomicIntegerArray() {
        int tempValue = 0;
        int[] array = {1, 2, 3, 4, 5, 6};
        // 定义一个整数数组原子类实例，赋值到变量 i
        AtomicIntegerArray i = new AtomicIntegerArray(array);

        // 获取第0个元素，然后设置为2
        tempValue = i.getAndSet(0, 2);
        // tempValue:1;  i:[2, 2, 3, 4, 5, 6]
        Print.fo("tempValue:" + tempValue + ";  i:" + i);

        // 获取第0个元素，然后自增
        tempValue = i.getAndIncrement(0);
        // tempValue:2;  i:[3, 2, 3, 4, 5, 6]
        Print.fo("tempValue:" + tempValue + ";  i:" + i);

        // 获取第0个元素，然后增加一个delta 5
        tempValue = i.getAndAdd(0, 5);
        // tempValue:3;  i:[8, 2, 3, 4, 5, 6]
        Print.fo("tempValue:" + tempValue + ";  i:" + i);
    }

    /**
     * 引用类型原子类 AtomicReference 测试
     */
    @Test
    public void testAtomicReference() {
        // 包装的原子对象
        AtomicReference<User> userRef = new AtomicReference<>();
        User user = new User("1", "张三");
        userRef.set(user);
        Print.tco("userRef is " + userRef.get());

        // 要使用CAS替换的User对象
        User updateUser = new User("2", "李四");
        // 使用CAS替换
        boolean success = userRef.compareAndSet(user, updateUser);
        Print.tco(" cas result is:" + success);
        Print.tco(" after cas,userRef is:" + userRef.get());
    }

    /**
     * 属性更新原子类 AtomicIntegerFieldUpdater 测试
     */
    @Test
    public void testAtomicIntegerFieldUpdater() throws InterruptedException {
        // 使用静态方法 newUpdater() 创建一个更新器 updater
        AtomicIntegerFieldUpdater<User> a = AtomicIntegerFieldUpdater.newUpdater(User.class, "age");
        User user = new User("1", "张三");
        // 使用属性更新器的 getAndIncrement、getAndAdd 增加 user 的 age 值
        Print.tco(a.getAndIncrement(user)); // 0
        Print.tco(a.getAndAdd(user, 100)); // 1
        Print.tco(a.get(user)); // 101
    }

    /**
     * 使用 AtomicStampedReference 解决 ABA 问题
     */
    @Test
    public void testAtomicStampedReference() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        AtomicStampedReference<Integer> atomicStampedRef = new AtomicStampedReference<>(1, 0);
        ThreadUtil.getMixedTargetThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                int stamp = atomicStampedRef.getStamp();
                Print.tco("before sleep 500: value=" + atomicStampedRef.getReference()
                        + " stamp=" + atomicStampedRef.getStamp());
                sleepMilliSeconds(500);
                success = atomicStampedRef.compareAndSet(
                        1,   // 原CAS中的原参数
                        10,      // 要替换后的新参数
                        stamp,                // 原CAS数据旧的版本号
                        stamp + 1); // 替换后的版本号
                Print.tco("after sleep 500 cas 1: success=" + success
                        + " value=" + atomicStampedRef.getReference()
                        + " stamp=" + atomicStampedRef.getStamp());
                stamp++;
                success = atomicStampedRef.compareAndSet(10, 1, stamp, stamp + 1);
                Print.tco("after  sleep 500 cas 2: success=" + success
                        + " value=" + atomicStampedRef.getReference()
                        + " stamp=" + atomicStampedRef.getStamp());
                latch.countDown();
            }
        });

        ThreadUtil.getMixedTargetThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                int stamp = atomicStampedRef.getStamp();
                Print.tco("before sleep 1000: value=" + atomicStampedRef.getReference()
                        + " stamp=" + atomicStampedRef.getStamp());
                sleepMilliSeconds(1000);
                success = atomicStampedRef.compareAndSet(1, 20, stamp, stamp + 1);
                Print.tco("after sleep 1000: stamp = " + atomicStampedRef.getStamp());
                success = atomicStampedRef.compareAndSet(1, 20, stamp, stamp++);
                Print.tco("after cas 3 1000: success=" + success
                        + " value=" + atomicStampedRef.getReference()
                        + " stamp=" + atomicStampedRef.getStamp());
                latch.countDown();
            }
        });
        latch.await();
    }

}
