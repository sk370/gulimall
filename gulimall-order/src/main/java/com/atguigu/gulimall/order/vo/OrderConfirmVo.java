package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className OrderConfirmVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/07 21:53
 */

public class OrderConfirmVo {
    // 收获地址列表
    List<MemberAddressVo> addresses;
    // 所有选中的购物项
    List<OrderItemVo> orderItems;
    // 发票信息【本项目不实现】
     // 积分（只实现积分优惠）
    Integer integration;

    BigDecimal total;//订单总额
    BigDecimal payPrice;//应付总额

    public String getOrderToken() {
        return orderToken;
    }

    public void setOrderToken(String orderToken) {
        this.orderToken = orderToken;
    }

    String orderToken;//订单防重令牌（防止网络补偿，多次点击提交订单——幂等性）
    Map<Long, Boolean> stocks;

    public  Integer getCount(){
        Integer i = 0;
        if(this.orderItems != null){
            for (OrderItemVo orderItem : orderItems) {
                i += orderItem.getCount();
            }
        }
        return i;
    }

    public BigDecimal getTotal() {
        BigDecimal bigDecimal = new BigDecimal("0");
        if(this.orderItems != null){
            for (OrderItemVo orderItem : orderItems) {
                BigDecimal multiply = orderItem.getPrice().multiply(new BigDecimal(orderItem.getCount()));
                bigDecimal = bigDecimal.add(multiply);
            }
        }
        return bigDecimal;
    }

    public BigDecimal getPayPrice() {
        BigDecimal bigDecimal = new BigDecimal("0");
        if(this.orderItems != null){
            for (OrderItemVo orderItem : orderItems) {
                BigDecimal multiply = orderItem.getPrice().multiply(new BigDecimal(orderItem.getCount()));
                bigDecimal = bigDecimal.add(multiply);
            }
        }

        // 应当再减去积分优惠
        return bigDecimal;
    }

    public List<MemberAddressVo> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<MemberAddressVo> addresses) {
        this.addresses = addresses;
    }

    public List<OrderItemVo> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemVo> orderItems) {
        this.orderItems = orderItems;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }


    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public void setStocks(Map<Long, Boolean> stocks) {
        this.stocks = stocks;
    }

    public Map<Long, Boolean> getStocks() {
        return stocks;
    }
}
