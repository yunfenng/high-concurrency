package cn.jaa.producerandcomsumer.store;

import cn.jaa.util.Print;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Jaa
 * @Description: 安全的共享数据区
 * @Date 2024/4/9
 */
public class SafeDataBuffer<T> {

    public static final int MAX_AMOUNT = 10;
    private BlockingDeque<T> dataList = new LinkedBlockingDeque<>();
    // 保存数量
    private AtomicInteger amount = new AtomicInteger(0);

    /**
     * 向数据区增加一个元素
     */
    public synchronized void add(T element) throws Exception {
        if (amount.get() > MAX_AMOUNT) {
            Print.tcfo("队列已经满了！");
            return;
        }
        dataList.add(element);
        Print.tcfo(element + "");
        amount.incrementAndGet();

        //如果数据不一致，抛出异常
        if (amount.get() != dataList.size()) {
            throw new Exception(amount + "!=" + dataList.size());
        }
    }

    /**
     * 从数据区获取一个元素
     */
    public synchronized T fetch() throws Exception {
        if (amount.get() <= 0) {
            Print.tcfo("队列已经空了！");
            return null;
        }
        T element = dataList.removeFirst();
        Print.tcfo(element + "");
        amount.decrementAndGet();
        // 如果数据不一致，抛出异常
        if (amount.get() != dataList.size()) {
            throw new Exception(amount + "!=" + dataList.size());
        }
        return element;
    }

}
