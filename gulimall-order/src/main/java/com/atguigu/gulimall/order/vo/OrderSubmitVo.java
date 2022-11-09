package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className OrderSubmitVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/08 16:47
 */
@Data
public class OrderSubmitVo {
    private Long addrId;//收获地址id
    private Integer payType;//支付方式
    // 无需提交需要购买的商品，去购物车再获取一遍，保证最新的价格以及库存信息
    private String orderToken;//防重令牌
    private BigDecimal payPrice;//订单价格，用于给用户提示确认价格，可以不用这个
    // todo 优惠、发票等未做
    // 用户信息在session中存放，不需要提交
    private String note;//订单备注
}
