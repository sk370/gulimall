package com.atguigu.common.to.es;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * ES中mapping对应的数据对象
 * @author zhuyuqi
 * @version v0.0.1
 * @className SkuESModel
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/26 22:47
 */
@Data
public class SkuESModel {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private String brandImg;
    private Long catalogId;
    private String brandName;
    private String catalogName;
    private List<Attrs> attrs;

    @Data
    public static class Attrs{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
