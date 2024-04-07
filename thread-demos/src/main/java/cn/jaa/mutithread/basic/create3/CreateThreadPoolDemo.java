package cn.jaa.mutithread.basic.create3;

import cn.jaa.util.Print;
import cn.jaa.util.RandomUtil;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.jaa.util.ThreadUtil.sleepMilliSeconds;
import static cn.jaa.util.ThreadUtil.sleepSeconds;

/**
 * @Author: Jaa
 * @Description: Executors的 4 种快捷创建线程池的方法
 * @Date 2024/4/7
 * 1) newSingleThreadExecutor创建“单线程化线程池”
 * 2) newFixedThreadPool创建“固定数量的线程池”
 */
public class CreateThreadPoolDemo {

    public static final int SLEEP_GAP = 500;

    /**
     * 异步任务执行的目标类
     */
    static class TargetTask implements Runnable {
        static AtomicInteger taskNo = new AtomicInteger(1);
        private String taskName;

        public TargetTask() {
            taskName = "task-" + taskNo.get();
            taskNo.incrementAndGet();
        }

        @Override
        public void run() {
            Print.tco("任务：" + taskName + " doing");
            // 线程睡眠一会
            sleepMilliSeconds(SLEEP_GAP);
            Print.tco(taskName + " 运行结束.");
        }

        public String toString() {
            return "TargetTask{" + taskName + '}';
        }
    }

    /**
     * 测试用例：只有一个线程的线程池
     */
    @Test
    public void testSingleThreadExecutor() {
        // 创建线程池
        ExecutorService pool = Executors.newSingleThreadExecutor();
        // 提交任务
        for (int i = 0; i < 5; i++) {
            pool.execute(new TargetTask());
            pool.submit(new TargetTask());
        }
        sleepSeconds(1000);
        // 关闭线程池
        pool.shutdown();
    }

    /**
     * 测试用例：只有3条线程固定大小的线程池
     */
    @Test
    public void testNewFixedThreadPool() {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 5; i++) {
            pool.execute(new TargetTask());
            pool.submit(new TargetTask());
        }
        sleepSeconds(1000);
        // 关闭线程池
        pool.shutdown();
    }

    /**
     * 测试用例：“可缓存线程池”
     */
    @Test
    public void testNewCacheThreadPool() {
        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            pool.execute(new TargetTask());
            pool.submit(new TargetTask());
        }
        sleepSeconds(1000);
        // 关闭线程池
        pool.shutdown();
    }

    /**
     * 测试用例：“可调度线程池”
     */
    @Test
    public void testNewScheduledThreadPool() {
        ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);
        for (int i = 0; i < 2; i++) {
            scheduled.scheduleAtFixedRate(new TargetTask(), 0, 500, TimeUnit.MILLISECONDS);
            // 以上的参数中：
            // 0表示首次执行任务的延迟时间，500表示每次执行任务的间隔时间
            // TimeUnit.MILLISECONDS 所设置的时间的计时单位为毫秒
        }
        sleepSeconds(1000);
        // 关闭线程池
        scheduled.shutdown();
    }

    /**
     * 测试用例：“可调度线程池2”
     */
    @Test
    public void testNewScheduledThreadPool2() {
        ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(0);
        for (int i = 0; i < 2; i++) {
            scheduled.scheduleAtFixedRate(new TargetTask(), 0, 500, TimeUnit.MILLISECONDS);
            // 以上的参数中：
            // 0表示首次执行任务的延迟时间，500表示每次执行任务的间隔时间
            // TimeUnit.MILLISECONDS所设置的时间的计时单位为毫秒
        }
        sleepSeconds(1000);
        // 关闭线程池
        scheduled.shutdown();
    }

    /**
     * 通过submit()返回的Future对象获取结果
     * 测试用例：获取异步调用的结果
     */
    @Test
    public void testSubmit2() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        Future<Integer> future = pool.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return RandomUtil.randInRange(200, 300);
            }
        });

        try {
            Integer result = future.get();
            Print.tco("异步执行的结果是:" + result);
        } catch (InterruptedException e) {
            Print.tco("异步调用被中断");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Print.tco("异步调用过程中，发生了异常");
            e.printStackTrace();
        }
        sleepSeconds(10);
        // 关闭线程池
        pool.shutdown();
    }
}
