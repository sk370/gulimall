package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 10:40:49
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
