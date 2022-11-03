package com.atguigu.gulimall.product.service;

import java.util.List;
import java.util.Map;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 品牌
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);

    /**
     * 自定义方法
     * @param brandIds
     * @return
     */
    List<BrandEntity> getBrandsById(List<Long> brandIds);
}

