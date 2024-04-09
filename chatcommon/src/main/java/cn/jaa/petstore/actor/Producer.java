package cn.jaa.petstore.actor;

import cn.jaa.util.Print;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.jaa.util.ThreadUtil.sleepMilliSeconds;

/**
 * @Author: Jaa
 * @Description: 通用的生产者定义
 * @Date 2024/4/9
 */
public class Producer implements Runnable {

    // 生产的时间间隔，生产一次等待的时间，默认为200毫秒
    public static final int PRODUCER_GAP = 200;
    // 总次数
    static final AtomicInteger TURN = new AtomicInteger(0);
    // 生产者对象编号
    static final AtomicInteger PRODUCER_NO = new AtomicInteger(1);
    // 生产者名称
    String name = null;
    // 生产者动作
    Callable action = null;
    int gap = PRODUCER_GAP;

    public Producer(Callable action, int gap) {
        this.action = action;
        this.gap = gap;
        name = "Producer-" + PRODUCER_NO.incrementAndGet();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 执行生产动作
                Object out = action.call();
                // 输出生产的结果
                if (null != out) {
                    Print.tcfo("第" + TURN.get() + "轮生产：" + out);
                }
                // 每一轮生产之后，稍微等待一下
                sleepMilliSeconds(gap);
                // 增加生产轮次
                TURN.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
