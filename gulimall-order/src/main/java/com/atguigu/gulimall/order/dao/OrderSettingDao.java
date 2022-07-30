package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:03:01
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
