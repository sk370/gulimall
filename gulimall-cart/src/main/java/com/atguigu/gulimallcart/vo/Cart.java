package com.atguigu.gulimallcart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车
 * @author zhuyuqi
 * @version v0.0.1
 * @className Cart
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/05 13:19
 */
public class Cart {
    private List<CartItem> items;
    private Integer countType;//商品类型
    private Integer countNum;//商品总数
    private BigDecimal totalAmount;//商品总价
    private BigDecimal reduce = new BigDecimal("0.00");//优惠加尔

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountType() {
        return countType = this.items.size();
    }

    public Integer getCountNum() {
        int count = 0;
        if(items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        countNum = count;
        return countNum;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0.00");

        if(items != null && items.size() > 0) {
            for (CartItem item : items) {
                if(item.getCheck()) {
                    amount = amount.add(item.getTotalPrice());
                }
            }
        }
        totalAmount = amount.subtract(getReduce());
        return totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }
}
