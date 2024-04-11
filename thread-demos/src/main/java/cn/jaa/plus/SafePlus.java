package cn.jaa.plus;

/**
 * 自增，有锁
 */
public class SafePlus {
    private Integer amount = 0;

    public void selfPlus() {
        synchronized (this) {
            amount++;
        }
    }

    public Integer getAmount() {
        return amount;
    }


}

