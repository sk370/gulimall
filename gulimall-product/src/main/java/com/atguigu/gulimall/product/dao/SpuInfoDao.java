package com.atguigu.gulimall.product.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atguigu.gulimall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * spu信息
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {
    /**
     * 自定义sql
     * @param spuId
     * @param code
     */
    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
