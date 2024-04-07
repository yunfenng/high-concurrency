package cn.jaa.mutithread.basic.create3;

import cn.jaa.util.Print;
import cn.jaa.util.RandomUtil;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
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
     * 异步的执行目标类：执行过程中将发生异常
     */
    static class TargetTaskWithError extends TargetTask {
        private String taskName;

        public void run() {
            super.run();
            throw new RuntimeException("Error from " + taskName);
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
     * 通过submit()返回的Future对象捕获异常
     */
    @Test
    public void testSubmit() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        pool.execute(new TargetTaskWithError());
        /**
         * submit(Runnable x) 返回一个future。可以用这个future来判断任务是否成功完成
         */
        Future future = pool.submit(new TargetTaskWithError());
        try {
            // 如果异常抛出，会在调用Future.get()时传递给调用者
            if (future.get() == null) {
                // 如果Future的返回为null，任务完成
                Print.tco("任务完成");
            }
        } catch (Exception e) {
            Print.tco(e.getCause().getMessage());
        }
        sleepSeconds(10);
        // 关闭线程池
        pool.shutdown();
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

    /**
     * 错误的线程池配置示例
     * <p>
     * 示例创建了最大线程数量maximumPoolSize为100的线程池，仅仅向其中提交了5个任务
     * 理论上，这5个任务都会被执行到，奇怪的是示例中只有1个任务在执行，其他的4个任务都在等待。
     * 其他任务被加入到了阻塞队列中，需要等pool-1-thread-1线程执行完第一个任务后，才能依次从阻塞队列取出执行。
     * 但是，实例中的第一个任务是一个永远也没有办法完成的任务，所以其他的4个任务只能永远在阻塞队列中等待着。
     * 由于参数配置得不合理，因此出现了以上的奇怪现象。
     * <p>
     * 为什么会出现上面的奇怪现象呢？
     * 因为例子中的corePoolSize为1，阻塞队列的大小为100，
     * 按照线程创建的规则，需要等阻塞队列已满，才会去创建新的线程。
     * 例子中加入了5个任务，阻塞队列大小为4（<100），所以线程池的调度器不会去创建新的线程，后面的4个任务只能等待。
     * <p>
     * （1）核心和最大线程数量、BlockingQueue队列等参数如果配置得不合理，可能会造成异步任务得不到预期的并发执行，造成严重的排队等待现象。
     * （2）线程池的调度器创建线程的一条重要的规则是：在corePoolSize已满之后，还需要等阻塞队列已满，才会去创建新的线程。
     */
    @Test
    public void testThreadPoolExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,
                100,
                100,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(100));
        for (int i = 0; i < 5; i++) {
            final int taskIndex = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Print.tco("taskIndex = " + taskIndex);
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        while (true) {
            Print.tco("- activeCount:" + executor.getActiveCount() + " - taskCount:" + executor.getTaskCount());
            sleepSeconds(1);
        }
    }

    /**
     * 简单线程工厂类，实现 ThreadFactory 接口
     * <p>
     * Executors 为线程池工厂类，用于快捷创建线程池（Thread Pool）；
     * ThreadFactory 为线程工厂类，用于创建线程（Thread）。
     */
    public static class SimpleThreadFactory implements ThreadFactory {

        static AtomicInteger threadNo = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable target) {
            String threadName = "simpleThread-" + threadNo.get();
            Print.tco("创建一条线程，名称为：" + threadName);
            threadNo.incrementAndGet();
            Thread thread = new Thread(target, threadName);
            thread.setDaemon(true);
            return thread;
        }
    }

    /**
     * 线程工厂的测试用例
     */
    @Test
    public void testThreadFactory() {
        // 使用自定义线程工厂，快捷创建一个固定大小的线程池
        ExecutorService pool = Executors.newFixedThreadPool(2, new SimpleThreadFactory());
        for (int i = 0; i < 5; i++) {
            pool.submit(new TargetTask());
        }
        // 等待10秒
        sleepSeconds(10);
        Print.tco("关闭线程池");
        pool.shutdown();
    }

    // 线程本地变量,用于记录线程异步任务的开始执行时间
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    /**
     * 调度器的钩子方法
     */
    @Test
    public void testHooks() {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2,
                4,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2)) {
            @Override
            protected void beforeExecute(Thread t, Runnable target) {
                Print.tco(target + "前钩子被执行");
                // 记录开始执行时间
                START_TIME.set(System.currentTimeMillis());
                super.beforeExecute(t, target);
            }

            @Override
            protected void afterExecute(Runnable target, Throwable t) {
                super.afterExecute(target, t);
                // 计算执行时长
                long time = (System.currentTimeMillis() - START_TIME.get());
                Print.tco(target + " 后钩子被执行, 任务执行时长（ms）：" + time);
                // 清空本地变量
                START_TIME.remove();
            }

            @Override
            protected void terminated() {
                Print.tco("调度器已经终止");
            }
        };
        for (int i = 0; i < 5; i++) {
            pool.execute(new TargetTask());
        }
        // 等待10秒
        sleepSeconds(10);
        Print.tco("关闭线程池");
        pool.shutdown();
    }
}
