package cn.jaa.mutithread.basic.create;

import cn.jaa.util.Print;
import cn.jaa.util.ThreadUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Jaa
 * @Description: 逻辑与数据更好的分离
 * @Date 2024/4/5
 */
public class SalesDemo {

    public static final int MAX_AMOUNT = 5; // 商品数量

    // 商店商品类（销售线程类），一个商品一个销售线程，每个线程异步销售4次
    static class StoreGoods extends Thread {
        public StoreGoods(String name) {
            super(name);
        }

        private int goodAmount = MAX_AMOUNT;

        public void run() {
            for (int i = 0; i <= MAX_AMOUNT; i++) {
                if (this.goodAmount > 0) {
                    Print.cfo(ThreadUtil.getCurThreadName() + "卖出一件，还剩" + (--goodAmount));
                    ThreadUtil.sleepMilliSeconds(10);
                }
            }
            Print.cfo(ThreadUtil.getCurThreadName() + " 运行结束.");
        }
    }

    // 商场商品类（target销售线程的目标类），一个商品最多销售4次，可以多人销售
    static class MallGoods implements Runnable {
        // 多人销售可能导致数据出错，使用原子数据类型保障数据安全
        private AtomicInteger goodAmount = new AtomicInteger(MAX_AMOUNT);

        @Override
        public void run() {
            for (int i = 0; i <= MAX_AMOUNT; i++) {
                if (this.goodAmount.get() > 0) {
                    Print.cfo(ThreadUtil.getCurThreadName() + "卖出一件，还剩" + (goodAmount.decrementAndGet()));
                    ThreadUtil.sleepMilliSeconds(10);
                }
            }
            Print.cfo(ThreadUtil.getCurThreadName() + " 运行结束.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Print.hint("商店版本的销售");
        for (int i = 1; i <= 2; i++) {
            Thread thread = null;
            thread = new StoreGoods("店员-" + i);
            thread.start();
        }
        Thread.sleep(2000);

        Print.hint("商场版本的销售");
        MallGoods mallGoods = new MallGoods();
        for (int i = 1; i <= 2; i++) {
            Thread thread = null;
            thread = new Thread(mallGoods, "商场销售员-" + i);
            thread.start();
        }
        Print.cfo(ThreadUtil.getCurThreadName() + " 运行结束.");

        /**
         * 通过对比可以看出：
         *    （1）通过继承Thread类实现多线程能更好地做到多个线程并发地完成各自的任务，访问各自的数据资源。
         *    （2）通过实现Runnable接口实现多线程能更好地做到多个线程并发地完成同一个任务，访问同一份数据资源。
         *        多个线程的代码逻辑可以方便地访问和处理同一个共享数据资源（如例子中的MallGoods.goodsAmount），
         *        这样可以将线程逻辑和业务数据进行有效的分离，更好地体现了面向对象的设计思想。
         *    （3）通过实现Runnable接口实现多线程时，如果数据资源存在多线程共享的情况，
         *        那么数据共享资源需要使用原子类型（而不是普通数据类型），
         *        或者需要进行线程的同步控制，以保证对共享数据操作时不会出现线程安全问题。
         */
    }

}
