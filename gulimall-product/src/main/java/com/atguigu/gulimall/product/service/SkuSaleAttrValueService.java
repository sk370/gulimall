package com.atguigu.gulimall.product.service;

import java.util.List;
import java.util.Map;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * sku销售属性&值
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 自定义方法
     * @param spuId
     * @return
     */
    List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId);
}

