package cn.jaa.mutithread.basic.create;

import cn.jaa.util.Print;
import cn.jaa.util.ThreadUtil;

/**
 * @Author: Jaa
 * @Description: 实现 Runnable 接口创建和启动线程
 * @Date 2024/4/5
 *  缺点：
 * （1）所创建的类并不是线程类，而是线程的target执行目标类，需要将其实例作为参数传入线程类的构造器，才能创建真正的线程。
 * （2）如果访问当前线程的属性（甚至控制当前线程），不能直接访问Thread的实例方法，
 *      必须通过Thread.currentThread()获取当前线程实例，才能访问和控制当前线程。
 *
 *  优点：
 *  （1）可以避免由于Java单继承带来的局限性。如果异步逻辑所在类已经继承了一个基类，就没有办法再继承Thread类。
 *      比如，当一个Dog类继承了Pet类，再要继承Thread类就不行了。所以在已经存在继承关系的情况下，只能使用实现Runnable接口的方式。
 *  （2）逻辑和数据更好分离。通过实现Runnable接口的方法创建多线程更加适合同一个资源被多段业务逻辑并行处理的场景。
 *      在同一个资源被多个线程逻辑异步、并行处理的场景中，通过实现Runnable接口的方式设计多个target执行目标类可以更加方便、
 *      清晰地将执行逻辑和数据存储分离，更好地体现了面向对象的设计思想。
 */
public class CreateDemo2 {

    public static final int MAX_TURN = 5;
    static int threadNo = 1;

    static class RunnableTarget implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i < MAX_TURN; i++) {
                Print.cfo(ThreadUtil.getCurThreadName() + ", 轮次: " + i);
            }
            Print.cfo(ThreadUtil.getCurThreadName() + " 运行结束.");
        }
    }

    public static void main(String[] args) {
        Thread thread = null;

        // 方法2.1：使用实现Runnable的实现类创建和启动线程
        for (int i = 0; i < 2; i++) {
            RunnableTarget target = new RunnableTarget();
            // 通过 Thread 类创建线程对象，将 Runnable 实例作为实际参数传入
            thread = new Thread(target, "RunnableThread-" + threadNo++);
            thread.start();
        }

        // 方法2.2：使用实现Runnable的匿名类创建和启动线程
        for (int i = 0; i < 2; i++) {
            thread = new Thread(new Runnable() { // 匿名实例
                @Override
                public void run() { // 异步执行的逻辑
                    for (int j = 1; j < MAX_TURN; j++) {
                        Print.cfo(ThreadUtil.getCurThreadName() + ", 轮次: " + j);
                    }
                    Print.cfo(ThreadUtil.getCurThreadName() + " 运行结束.");
                }
            }, "RunnableThread-" + threadNo++);
            thread.start();
        }

        // 方法2.3：使用实现 lambda 表达式创建和启动线程
        for (int i = 0; i < 2; i++) {
            thread = new Thread(() -> { // 匿名实例
                // 异步执行的逻辑
                for (int j = 1; j < MAX_TURN; j++) {
                    Print.cfo(ThreadUtil.getCurThreadName() + ", 轮次: " + j);
                }
                Print.cfo(ThreadUtil.getCurThreadName() + " 运行结束.");
            }, "RunnableThread-" + threadNo++);
            thread.start();
        }

    }
}
