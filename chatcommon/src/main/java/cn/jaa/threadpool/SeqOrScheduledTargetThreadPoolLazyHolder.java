package cn.jaa.threadpool;

import cn.jaa.util.ShutdownHookThread;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static cn.jaa.util.ThreadUtil.CustomThreadFactory;
import static cn.jaa.util.ThreadUtil.shutdownThreadPoolGracefully;

/**
 * @Author: Jaa
 * @Description: 懒汉式单例创建线程池：用于定时任务、顺序排队执行任务
 * @Date 2024/4/5
 */
@Slf4j
public class SeqOrScheduledTargetThreadPoolLazyHolder {

    // 线程池：用于定时任务、顺序排队执行任务
    private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("seq"));

    public static ScheduledThreadPoolExecutor getInnerExecutor() {
        return EXECUTOR;
    }

    static {
        log.info("线程池已经初始化");

        //JVM关闭时的钩子函数
        Runtime.getRuntime().addShutdownHook(
                new ShutdownHookThread("定时和顺序任务线程池", new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        //优雅关闭线程池
                        shutdownThreadPoolGracefully(EXECUTOR);
                        return null;
                    }
                }));
    }

}
