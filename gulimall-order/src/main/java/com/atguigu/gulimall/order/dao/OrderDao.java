package com.atguigu.gulimall.order.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:03:01
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
    /**
     * 自定义sql
     * @param orderSn
     * @param code
     */
    void updateOrderStatus(@Param("orderSn") String orderSn, @Param("code") Integer code);
}
