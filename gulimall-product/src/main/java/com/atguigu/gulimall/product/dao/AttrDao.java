package com.atguigu.gulimall.product.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 商品属性
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
    /**
     * 自定义查询:找到可查询的attrid
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrIds(@Param("attrIds") List<Long> attrIds);
}
