package com.atguigu.gulimall.coupon.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 秒杀活动商品关联
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 10:40:49
 */
@Mapper
public interface SeckillSkuRelationDao extends BaseMapper<SeckillSkuRelationEntity> {
	
}
