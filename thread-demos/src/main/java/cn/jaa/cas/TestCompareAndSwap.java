package cn.jaa.cas;

import cn.jaa.util.JvmUtil;
import cn.jaa.util.Print;
import cn.jaa.util.ThreadUtil;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;
import sun.misc.Unsafe;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: Jaa
 * @Description: 基于CAS无锁实现安全自增
 * @Date 2024/4/11
 */
public class TestCompareAndSwap {

    /**
     * 基于CAS无锁实现安全自增
     */
    static class OptimisticLockingPlus {
        // 并发数量
        private static final int THREAD_COUNT = 10;
        // 内部值，使用 volatile 保证线程可见性
        private volatile int value;
        // 不安全类
        private static final Unsafe unsafe = JvmUtil.getUnsafe();
        // value 的内存偏移（相对于对象头部的偏移，不是绝对偏移）
        private static final long valueOffset;
        // 统计失败的次数
        private static final AtomicLong failure = new AtomicLong(0);

        static {
            try {
                // 取得 value 属性的内存偏移
                valueOffset = unsafe.objectFieldOffset(OptimisticLockingPlus.class.getDeclaredField("value"));
                Print.tco("valueOffset:=" + valueOffset);
            } catch (Exception ex) {
                throw new Error(ex);
            }
        }

        /**
         * 通过 CAS 原子操作，进行 “比较并交换”
         *
         * @param oldValue
         * @param newValue
         * @return
         */
        private final boolean unSafeCompareAndSet(int oldValue, int newValue) {
            return unsafe.compareAndSwapInt(this, valueOffset, oldValue, newValue);
        }

        /**
         * 使用无锁编程实现安全的自增方法
         */
        public void selfPlus1() {
            // 获取旧值
            int oldValue = value;
            int i = 0;
            // 通过CAS原子操作，如果操作失败则自旋，一直到操作成功
            do {
                oldValue = value;
                // 统计无效的自旋次数
                if (i++ > 1) {
                    failure.incrementAndGet();
                }
            } while (!unSafeCompareAndSet(oldValue, oldValue + 1));
        }

        public static void main(String[] args) throws InterruptedException {
            final OptimisticLockingPlus cas = new OptimisticLockingPlus();
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
            for (int i = 0; i < THREAD_COUNT; i++) {
                // 创建10个线程,模拟多线程环境
                ThreadUtil.getMixedTargetThreadPool().submit(() -> {
                    for (int j = 0; j < 1000; j++) {
                        cas.selfPlus1();
                    }
                    latch.countDown();
                });
            }
            latch.await();
            Print.tco("累加之和：" + cas.value);
            Print.tco("失败次数：" + cas.failure.get());
        }
    }

    @Test
    public void printObjectStruct() {
        // 创建一个对象
        OptimisticLockingPlus object = new OptimisticLockingPlus();
        // 给成员赋值
        object.value = 100;
        // 通过JOL工具输出内存布局
        String printable = ClassLayout.parseInstance(object).toPrintable();
        Print.fo("object = " + printable);
    }

    // 从JOL输出的结果可以看出，一个TestCompareAndSwap对象的Object Header占用了12字节，
    // 而value属性的内存位置紧挨在Object Header之后，所以value属性的相对偏移量值为12

    // 08:49:52.300 [main] INFO cn.jaa.threadpool.SeqOrScheduledTargetThreadPoolLazyHolder - 线程池已经初始化
    // [main]：valueOffset:=12
    // [TestCompareAndSwap.printObjectStruct]：object = cn.jaa.cas.TestCompareAndSwap$OptimisticLockingPlus object internals:
    // OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
    //     0     4    (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
    //     4     4    (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
    //     8     4    (object header)                           f4 31 01 f8 (11110100 00110001 00000001 11111000) (-134139404)
    //     12    4    int OptimisticLockingPlus.value               100
    // Instance size: 16 bytes
    // Space losses: 0 bytes internal + 0 bytes external = 0 bytes total

}
