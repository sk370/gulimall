package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;
import com.atguigu.gulimall.product.vo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * spu信息
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 自定义方法
     * @param vo
     */
    void saveSpuInfo(SpuSaveVo vo);

    /**
     * 自定义方法
     * @param spuInfoEntity
     */
    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

}

