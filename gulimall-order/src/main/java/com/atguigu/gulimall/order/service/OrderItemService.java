package com.atguigu.gulimall.order.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:03:01
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

