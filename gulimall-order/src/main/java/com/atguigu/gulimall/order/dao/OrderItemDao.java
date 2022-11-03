package com.atguigu.gulimall.order.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 订单项信息
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:03:01
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
