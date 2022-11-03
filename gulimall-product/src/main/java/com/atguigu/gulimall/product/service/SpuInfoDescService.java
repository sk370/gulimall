package com.atguigu.gulimall.product.service;

import java.util.Map;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * spu信息介绍
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 自定义方法
     * @param spuInfoDescEntity
     */
    void saveSpuInfoDesc(SpuInfoDescEntity spuInfoDescEntity);
}

