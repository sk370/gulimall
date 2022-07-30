package com.atguigu.gulimall.order.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 退货原因
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:03:01
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

