package com.atguigu.gulimall.secondkill.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀的商品信息
 * @author zhuyuqi
 * @version v0.0.1
 * @className SkuInfoVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/11 16:47
 */
@Data
public class SkuInfoVo {

    private Long skuId;
    /**
     * spuId
     */
    private Long spuId;
    /**
     * sku名称
     */
    private String skuName;
    /**
     * sku介绍描述
     */
    private String skuDesc;
    /**
     * 所属分类id
     */
    private Long catalogId;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 默认图片
     */
    private String skuDefaultImg;
    /**
     * 标题
     */
    private String skuTitle;
    /**
     * 副标题
     */
    private String skuSubtitle;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 销量
     */
    private Long saleCount;
}
