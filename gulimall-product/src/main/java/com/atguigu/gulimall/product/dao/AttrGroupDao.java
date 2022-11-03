package com.atguigu.gulimall.product.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 属性分组
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {
    /**
     * 自定义sql
     * @param spuId
     * @param catalogId
     * @return
     */
    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
