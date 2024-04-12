package cn.jaa.cas;

import cn.jaa.util.JvmUtil;
import cn.jaa.util.Print;
import cn.jaa.util.ThreadUtil;
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

}
