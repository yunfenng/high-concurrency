package cn.jaa.mutithread.basic.create;

import cn.jaa.util.Print;

import static cn.jaa.util.ThreadUtil.getCurThreadName;

/**
 * @Author: Jaa
 * @Description:
 * @Date 2024/4/5
 */
public class EmptyThreadDemo {

    public static void main(String[] args) {
        // 使用Thread类创建和启动线程
        Thread thread = new Thread();
        Print.cfo("线程名称：" + thread.getName());
        Print.cfo("线程ID：" + thread.getId());
        Print.cfo("线程状态：" + thread.getState());
        Print.cfo("线程优先级：" + thread.getPriority());
        Print.cfo(getCurThreadName() + " 运行结束.");
        thread.start();
    }
}
