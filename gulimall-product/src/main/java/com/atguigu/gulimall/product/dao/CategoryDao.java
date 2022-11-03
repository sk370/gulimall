package com.atguigu.gulimall.product.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 商品三级分类
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
