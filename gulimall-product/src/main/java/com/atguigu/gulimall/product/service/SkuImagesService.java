package com.atguigu.gulimall.product.service;

import java.util.List;
import java.util.Map;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * sku图片
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 自定义方法
     * @param skuId
     * @return
     */
    List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

