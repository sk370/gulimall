package com.atguigu.gulimall.order.service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.atguigu.common.to.SecKillOrderTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVo;
import com.atguigu.gulimall.order.vo.pay.PayAsyncVo;
import com.atguigu.gulimall.order.vo.pay.PayVo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * 订单
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:03:01
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 自定义方法：给订单确认页返回需要的信息
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 自定义方法
     * @param vo
     * @return
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    /**
     * 自定义方法
     * @param orderSn
     * @return
     */
    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 自定义方法：未支付订单自动关闭订单
     * @param entity
     */
    void closeOrder(OrderEntity entity);

    /**
     * 自定义方法：获取当前订单的支付信息
     * @param orderSn
     * @return
     */
    PayVo getOrderPay(String orderSn);

    /**
     * 自定义方法：订单详情页
     * @param params
     * @return
     */
    PageUtils queryPageWithItem(Map<String, Object> params);

    /**
     * 自定义方法
     * @param vo
     * @param request
     * @return
     */
    String handlePayResult(PayAsyncVo vo,HttpServletRequest request);

    /**
     * 自定义方法
     * @param secKillOrder
     */
    void createSecKillOrder(SecKillOrderTo secKillOrder);
}

