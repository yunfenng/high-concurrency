package cn.jaa.cas;

import cn.jaa.util.Print;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Jaa
 * @Description: atomic 测试
 * @Date 2024/4/12
 */
public class AtomicTest {

    /**
     * 基础原子类 AtomicInteger 的使用示例
     */
    @Test
    public void atomicIntegerTest() {
        int tempValue = 0;
        // 定义一个整数原子类实例，赋值到变量 i
        AtomicInteger i = new AtomicInteger(0);

        // 取值，然后设置一个新值
        tempValue = i.getAndSet(3);
        Print.fo("tempValue:" + tempValue + ";  i:" + i.get()); // tempValue:0;  i:3

        //取值，然后自增
        tempValue = i.getAndIncrement();
        //输出
        Print.fo("tempValue:" + tempValue + ";  i:" + i.get()); // tempValue:3;  i:4

        // 取值，然后增加5
        tempValue = i.getAndAdd(5);
        Print.fo("tempValue:" + tempValue + ";  i:" + i.get()); // tempValue:4;  i:9

        // CAS交换
        boolean flag = i.compareAndSet(9, 100);
        // 输出
        Print.fo("flag:" + flag + ";  i:" + i.get()); // flag:true;  i:100
    }
}
