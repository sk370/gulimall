package com.atguigu.gulimall.secondkill.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀信息
 * @author zhuyuqi
 * @version v0.0.1
 * @className SecKillSkuVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/11 16:23
 */
@Data
public class SecKillSkuVo {

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
}
