package cn.jaa.mutithread.basic.create;

import cn.jaa.util.Print;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static cn.jaa.util.ThreadUtil.getCurThreadName;

/**
 * @Author: Jaa
 * @Description: 线程创建方法三：使用Callable和FutureTask创建线程
 * @Date 2024/4/5
 */
public class CreateDemo3 {
    public static final int MAX_TURN = 5;
    public static final int COMPUTE_TIMES = 100000000;

    // 1、创建一个 Callable 接口的实现类
    static class ReturnableTask implements Callable<Long> {
        public Long call() throws Exception {
            // 2、编写好异步执行的具体逻辑，返回值就是异步任务的执行结果
            long startTime = System.currentTimeMillis();
            Print.cfo(getCurThreadName() + " 线程运行开始...");
            Thread.sleep(1000);
            for (int i = 0; i < COMPUTE_TIMES; i++) {
                int j = i * 10000;
            }
            long used = System.currentTimeMillis() - startTime;
            Print.cfo(getCurThreadName() + " 线程运行结束...");
            return used;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 3、创建 Callable 类型任务对象
        ReturnableTask task = new ReturnableTask();
        // 4、提交 Callable 类型任务对象，获取 Future 类型结果对象
        FutureTask<Long> futureTask = new FutureTask<>(task);
        Thread thread = new Thread(futureTask, "returnableThread"); // 5、
        thread.start(); // 6、启动线程

        Thread.sleep(500);
        Print.cfo(getCurThreadName() + " 让子弹飞一会儿.");
        Print.cfo(getCurThreadName() + " 做一点自己的事情.");
        for (int i = 0; i < COMPUTE_TIMES / 2; i++) {
            int j = i * 10000;
        }
        Print.cfo(getCurThreadName() + " 获取并发任务的执行结果.");

        try {
            Print.cfo(thread.getName() + " 线程占用时间：" + futureTask.get() + " ms");
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Print.cfo(getCurThreadName() + " 运行结束.");
    }


}
