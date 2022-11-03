package com.atguigu.gulimall.product.vo;

import java.util.List;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;

import lombok.Data;

/**
 * 详情页商品对象
 * @author zhuyuqi
 * @version v0.0.1
 * @className SkuItemVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/01 15:04
 */
@Data
public class SkuItemVo {
    // 1. sku基本信息获取 `pms_sku_info`表
    SkuInfoEntity info;
    boolean hasStock = true;//默认显示有货
    // 2. sku图片信息 `pms_sku_images`表
    List<SkuImagesEntity> images;
    // 3. 获取spu的销售属性组合——根据spu查找属性类型为销售属性的属性的组合
    /*
    选择颜色
        石墨色12promax 6.7英寸港版石墨色12promax 6.7英寸港版
        金色12promax 6.7英寸港版金色12promax 6.7英寸港版
        海蓝色12promax 6.7英寸港版海蓝色12promax 6.7英寸港版
        银色12promax 6.7英寸港版银色12promax 6.7英寸港版
        海蓝色 12pro 6.1英寸海蓝色 12pro 6.1英寸
        金色 12pro 6.1英寸金色 12pro 6.1英寸
        石墨色 12pro 6.1英寸石墨色 12pro 6.1英寸
        银色 12pro 6.1英寸银色 12pro 6.1英寸
    选择版本
        128GB未激活
        256GB未激活
        512GB未激活
    */
    List<SkuItemSaleAttrVo> saleAttr;
    // 4. 获取spu的介绍 `pms_spu_info_desc`
    SpuInfoDescEntity desp;
    // 5. 获取spu的规格参数信息
    /*
    主体      机型       Apple iPhone 12 Pro Max
            上市日期    2020-11-05
           入网型号     1
    基本信息    机身尺寸    宽78.1mm；长160.8mm；厚7.4mm
               CPU型号     A14
     */
    List<SpuItemAttrGroupVo> groupAttrs;


}
