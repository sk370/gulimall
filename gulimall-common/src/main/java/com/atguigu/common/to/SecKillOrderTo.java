package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className SecKillOrderTo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/12 13:10
 */
@Data
public class SecKillOrderTo {
    private String orderSn;//订单号
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 当前用户下单秒杀数量
     */
    private Integer num;
    private Long memberId;//会员id
}
