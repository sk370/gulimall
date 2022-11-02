package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className SkuItemSaleAttrVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/01 16:37
 */
@Data
public class SkuItemSaleAttrVo{
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
