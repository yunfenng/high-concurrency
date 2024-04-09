package cn.jaa.mutithread.producerandcomsumer.goods;

import cn.jaa.util.RandomUtil;

/**
 * @Author: Jaa
 * @Description:
 * @Date 2024/4/9
 */
public interface IGoods extends Comparable<IGoods> {

    void setId(int id);

    enum Type {
        PET,
        FOOD,
        CLOTHES;

        public static Type randType() {
            int length = values().length;
            int typeNo = RandomUtil.randInMod(length) - 1;
            return values()[typeNo];
        }
    }

    /**
     * 取得商品ID
     *
     * @return ID
     */
    int getID();

    /**
     * 取得商品价格
     *
     * @return 价格
     */
    float getPrice();


    /**
     * 设置商品价格
     */
    void setPrice(float price);

    /**
     * 取得商品名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 取得商品数量
     *
     * @return 数量
     */

    int getAmount();

    /**
     * 取得商品类型
     *
     * @return 类型
     */
    Type getType();
}
