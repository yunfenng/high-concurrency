package cn.jaa.plus;

/**
 * @Author: Jaa
 * @Description:
 * @Date 2024/4/8
 */
public class SafeStaticMethodPlus {

    // 静态的临界区资源
    private static Integer amount = 0;

    // 使用 synchronized 关键字修饰 static 静态方法
    // 属于类锁，因为 static 关键字修饰的静态方法，其锁对象为 类本身
    public static synchronized void selfPlus() {
        amount++;
    }

    public Integer getAmount() {
        return amount;
    }

}

