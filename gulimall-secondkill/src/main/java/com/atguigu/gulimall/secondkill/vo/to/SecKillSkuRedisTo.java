package com.atguigu.gulimall.secondkill.vo.to;

import com.atguigu.gulimall.secondkill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 给redis保存的信息，存多点，这样用的时候不用再去查sql
 * @author zhuyuqi
 * @version v0.0.1
 * @className SecKillSkuRedisTo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/11 16:45
 */
@Data
public class SecKillSkuRedisTo {


    /**
     * id
     */
    private Long id;
    /**
     * 活动id
     */
    private Long promotionId;
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
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    // Sku的详细信息
    private SkuInfoVo skuInfo;

    /**
     * 秒杀的开始时间
     */
    private Long startTime;

    /**
     * 秒杀的结束时间
     */
    private Long endTime;

    /**
     * 商品的随机码
     */
    private String randomCode;

}
