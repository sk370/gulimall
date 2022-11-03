package com.atguigu.gulimall.coupon.service;

import java.util.Map;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品满减信息
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 10:40:49
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 自定义方法
     * @param skuReductionTo
     */
    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

