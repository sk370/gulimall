package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderOperateHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作历史记录
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:03:01
 */
@Mapper
public interface OrderOperateHistoryDao extends BaseMapper<OrderOperateHistoryEntity> {
	
}
