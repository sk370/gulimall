package com.atguigu.gulimall.order.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.order.entity.OrderReturnApplyEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 订单退货申请
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:03:01
 */
@Mapper
public interface OrderReturnApplyDao extends BaseMapper<OrderReturnApplyEntity> {
	
}
