package com.atguigu.gulimall.order.listener;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className OrderCloseListener
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/10 14:21
 */
@Service
@RabbitListener(queues = "order.release.order.queue")//监听队列
public class OrderCloseListener {
    @Autowired
    OrderService orderService;

    /**
     * 监听指定队列接收到消息的情况
     * @param entity
     */
    @RabbitHandler
    public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {
        System.out.println("收到过期的订单信息，准备关闭订单：" + entity.getOrderSn());
        try {
            orderService.closeOrder(entity);//未支付订单自动关闭
            // TODO 手动调用支付宝收单（支付宝接口），让支付失败，防止支付过程中订单过期
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);//消息重回队列
        }
    }
}



