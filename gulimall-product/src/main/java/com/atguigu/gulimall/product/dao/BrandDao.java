package com.atguigu.gulimall.product.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 品牌
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {
	
}
