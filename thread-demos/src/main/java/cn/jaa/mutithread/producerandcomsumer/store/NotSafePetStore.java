package cn.jaa.mutithread.producerandcomsumer.store;

import cn.jaa.mutithread.producerandcomsumer.goods.Goods;
import cn.jaa.mutithread.producerandcomsumer.goods.IGoods;
import cn.jaa.petstore.actor.Consumer;
import cn.jaa.petstore.actor.Producer;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Jaa
 * @Description:
 * @Date 2024/4/9
 */
public class NotSafePetStore {

    // 数据缓存区静态实例
    private static NotSafeDataBuffer<IGoods> notSafeDataBuffer = new NotSafeDataBuffer<>();
    // 生产者执行的动作
    static Callable<IGoods> produceAction = () -> {
        // 首先生成一个随机的商品
        IGoods goods = Goods.produceOne();
        try {
            notSafeDataBuffer.add(goods);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goods;
    };

    // 消费者执行的动作
    static Callable<IGoods> consumeAction = () -> {
        // 从PetStore获取商品
        IGoods goods = null;
        try {
            goods = notSafeDataBuffer.fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goods;
    };

    public static void main(String[] args) {
        System.setErr(System.out);

        // 同时并发执行的线程数
        final int THREAD_TOTAL = 20;
        // 线程池，用于多线程模拟测试
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_TOTAL);
        for (int i = 0; i < 5; i++) {
            // 生产者线程每生产一个商品，间隔500ms
            threadPool.submit(new Producer(produceAction, 500));
            // 消费者线程每消费一个商品，间隔1500ms
            threadPool.submit(new Consumer(consumeAction, 1500));
        }
    }
}
