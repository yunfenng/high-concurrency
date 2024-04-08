package cn.jaa.mutithread.plus;

/**
 * @Author: Jaa
 * @Description: 自增, 无锁
 * @Date 2024/4/8
 */
public class NotSafePlus {

    private Integer amount = 0; // 临界区资源

    // 临界区代码段
    public void selfPlus() {
        amount++;
    }

    public Integer getAmount() {
        return amount;
    }
}
