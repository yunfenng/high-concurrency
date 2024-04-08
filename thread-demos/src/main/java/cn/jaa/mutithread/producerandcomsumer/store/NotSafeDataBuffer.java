package cn.jaa.mutithread.producerandcomsumer.store;

import cn.jaa.util.Print;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Jaa
 * @Description: 数据缓存区，不安全版本的定义
 * @Date 2024/4/8
 */
class NotSafeDataBuffer<T> {
    public static final int MAX_AMOUNT = 10;
    private List<T> dataList = new ArrayList<>();

    // 保存数量
    private AtomicInteger amount = new AtomicInteger(0);

    /**
     * 向数据区增加一个元素
     *
     * @param element
     */
    public void add(T element) {
        if (amount.get() >= MAX_AMOUNT) {
            Print.tcfo("队列已经满了");
            return;
        }
        dataList.add(element);
        Print.tcfo(element + " ");
        amount.incrementAndGet();

        // 如果数据不一致，抛出异常
        if (amount.get() != dataList.size()) {
            throw new RuntimeException(amount + "!=" + dataList.size());
        }
    }

    /**
     * 从数据区取出一个元素
     *
     * @return
     */
    public T fetch() {
        if (amount.get() <= 0) {
            Print.tcfo("队列已经空了");
            return null;
        }
        T element = dataList.remove(0);
        Print.tcfo(element + " ");
        amount.decrementAndGet();

        // 如果数据不一致，抛出异常
        if (amount.get() != dataList.size()) {
            throw new RuntimeException(amount + "!=" + dataList.size());
        }
        return element;
    }

}
