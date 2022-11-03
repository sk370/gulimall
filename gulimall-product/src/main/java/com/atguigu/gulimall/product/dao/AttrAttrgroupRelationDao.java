package com.atguigu.gulimall.product.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 属性&属性分组关联
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {
    /**
     * 自定义毗连删除方法
     * @param relationEntityList
     */
    void deleteBatchRelation(@Param("entities") List<AttrAttrgroupRelationEntity> relationEntityList);
}
