package cn.jaa.mutithread.basic.create;

import cn.jaa.util.Print;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static cn.jaa.util.ThreadUtil.getCurThreadName;
import static cn.jaa.util.ThreadUtil.sleepMilliSeconds;
import static cn.jaa.util.ThreadUtil.sleepSeconds;

/**
 * @Author: Jaa
 * @Description: 线程创建方法四：通过线程池创建线程
 * @Date 2024/4/5
 */
public class CreateDemo4 {

    public static final int MAX_TURN = 5;
    public static final int COMPUTE_TIMES = 100000000;

    // 创建一个包含三个线程的线程池
    private static ExecutorService pool = Executors.newFixedThreadPool(3);

    static class DemoThread implements Runnable {
        public void run() {
            for (int j = 1; j < MAX_TURN; j++) {
                Print.cfo(getCurThreadName() + ", 轮次：" + j);
                sleepMilliSeconds(10);
            }
        }
    }

    static class ReturnableTask implements Callable<Long> {
        // 返回并发执行的时间
        @Override
        public Long call() throws Exception {
            long startTime = System.currentTimeMillis();
            Print.cfo(getCurThreadName() + " 线程运行开始...");
            for (int j = 1; j < MAX_TURN; j++) {
                Print.cfo(getCurThreadName() + ", 轮次：" + j);
                sleepMilliSeconds(10);
            }
            long used = System.currentTimeMillis() - startTime;
            Print.cfo(getCurThreadName() + " 线程运行结束...");
            return used;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 执行线程实例，无返回值
        pool.execute(new DemoThread());

        // 执行Runnable执行目标实例
        pool.execute(new Runnable() {
            @Override
            public void run() {
                for (int j = 1; j < MAX_TURN; j++) {
                    Print.cfo(getCurThreadName() + ", 轮次：" + j);
                    sleepMilliSeconds(10);
                }
            }
        });

        // 提交Callable 执行目标实例, 有返回
        Future<Long> future = pool.submit(new ReturnableTask());
        Long result = future.get();
        Print.cfo("异步任务执行结果为：" + result);
        sleepSeconds(Integer.MAX_VALUE);
    }


}
