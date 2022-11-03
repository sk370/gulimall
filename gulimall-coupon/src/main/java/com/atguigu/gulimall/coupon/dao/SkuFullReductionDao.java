package com.atguigu.gulimall.coupon.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 商品满减信息
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 10:40:49
 */
@Mapper
public interface SkuFullReductionDao extends BaseMapper<SkuFullReductionEntity> {
	
}
