package cn.jaa.mutithread.producerandcomsumer.store;

import cn.jaa.mutithread.producerandcomsumer.goods.Goods;
import cn.jaa.mutithread.producerandcomsumer.goods.IGoods;
import cn.jaa.petstore.actor.Consumer;
import cn.jaa.petstore.actor.Producer;
import cn.jaa.util.JvmUtil;
import cn.jaa.util.Print;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Jaa
 * @Description: 使用“等待-通知”机制通信的生产者-消费者实现版本
 * @Date 2024/4/10
 *
 * 调用wait()和notify()系列方法进行线程通信的要点如下：
 * （1）调用某个同步对象locko的wait()和notify()类型方法前，必须要取得这个锁对象的监视锁，
 *      所以wait()和notify()类型方法必须放在synchronized(locko)同步块中，如果没有获得监视锁，JVM就会报IllegalMonitorStateException异常。
 * （2）调用wait()方法时使用while进行条件判断，如果是在某种条件下进行等待，对条件的判断就不能使用if语句做一次性判断，
 *      而是使用while循环进行反复判断。只有这样才能在线程被唤醒后继续检查wait的条件，并在条件没有满足的情况下继续等待。
 */
public class CommunicatePetStore {

    // 数据缓冲区的最大长度
    public static final int MAX_AMOUNT = 10;

    /**
     * 数据缓冲区，类定义
     *
     * @param <T>
     */
    static class DataBuffer<T> {
        // 保存数据
        private List<T> dataList = new ArrayList<>();
        // 数据缓冲区长度
        private Integer amount = 0;

        // 用于临界区同步，临界区资源为数据缓冲区的dataList变量和amount变量
        private final Object LOCK_OBJECT = new Object();
        // 用于数据缓冲区的未满条件等待和通知。
        // 生产者在添加元素前需要判断数据区是否已满，
        // 如果是，生产者就进入NOT_FULL的同步区等待被通知，只要消费者消耗一个元素，数据区就是未满的，进入NOT_FULL的同步区发送通知。
        private final Object NOT_FULL = new Object();
        // 用于数据缓冲区的非空条件等待和通知。
        // 消费者在消耗元素前需要判断数据区是否已空，
        // 如果是，消费者就进入NOT_EMPTY的同步区等待被通知，只要生产者添加一个元素，数据区就是非空的，生产者会进入NOT_EMPTY的同步区发送通知。
        private final Object NOT_EMPTY = new Object();

        /**
         * 向数据区增加一个元素
         *
         * @param element
         */
        public void add(T element) throws InterruptedException {
            while (amount >= MAX_AMOUNT) {
                // wait() 和 notify()类型方法必须放在synchronized(locko)同步块中，
                // 如果没有获得监视锁，JVM就会报IllegalMonitorStateException异常。
                synchronized (NOT_FULL) {
                    Print.tcfo("队列已经满了！");
                    // 等待未满通知
                    NOT_FULL.wait();
                }
            }
            synchronized (LOCK_OBJECT) {
                dataList.add(element);
                amount++;
            }
            synchronized (NOT_EMPTY) {
                // 发送未空通知
                NOT_EMPTY.notify();
            }
        }

        /**
         * 从数据区取出一个商品
         *
         * @return
         */
        public T fetch() throws InterruptedException {
            while (amount <= 0) {
                synchronized (NOT_EMPTY) {
                    Print.tcfo("队列已经空了！");
                    // 等待未空通知
                    NOT_EMPTY.wait();
                }
            }

            T element = null;
            synchronized (LOCK_OBJECT) {
                element = dataList.remove(0);
                amount--;
            }

            synchronized (NOT_FULL) {
                // 发送未满通知
                NOT_FULL.notify();
            }

            return element;
        }
    }

    public static void main(String[] args) {
        Print.cfo("当前进程的ID是" + JvmUtil.getProcessID());
        System.setErr(System.out);
        // 共享数据区，实例对象
        DataBuffer<IGoods> dataBuffer = new DataBuffer<>();

        // 生产者执行的动作
        Callable<IGoods> produceAction = () -> {
            // 首先生成一个随机的商品
            IGoods goods = Goods.produceOne();
            // 将商品加上共享数据区
            dataBuffer.add(goods);
            return goods;
        };

        // 消费者执行的动作
        Callable<IGoods> consumerAction = () -> {
            // 从PetStore获取商品
            IGoods goods = null;
            goods = dataBuffer.fetch();
            return goods;
        };

        // 同时并发执行的线程数
        final int THREAD_TOTAL = 20;

        //线程池，用于多线程模拟测试
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_TOTAL);

        // 假定共11个线程，其中有10个消费者，但是只有1个生产者
        final int CONSUMER_TOTAL = 11;
        final int PRODUCE_TOTAL = 1;

        for (int i = 0; i < PRODUCE_TOTAL; i++) {
            // 生产者线程每生产一个商品，间隔50毫秒
            threadPool.submit(new Producer(produceAction, 50));
        }
        for (int i = 0; i < CONSUMER_TOTAL; i++) {
            // 消费者线程每消费一个商品，间隔100毫秒
            threadPool.submit(new Consumer(consumerAction, 100));
        }
    }
}
