package com.atguigu.gulimall.order.listener;

import com.atguigu.common.to.SecKillOrderTo;
import com.atguigu.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className OrderSecKillListener
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/12 13:24
 */
@RabbitListener(queues = {"order.seckill.order.queue"})
@Component
public class OrderSecKillListener {
    @Autowired
    OrderService orderService;

    /**
     * 监听指定队列接收到消息的情况
     * @param secKillOrder
     */
    @RabbitHandler
    public void listener(SecKillOrderTo secKillOrder, Channel channel, Message message) throws IOException {
        System.out.println("准备创建秒杀单的详细信息：" + secKillOrder.getOrderSn());
        try {
            orderService.createSecKillOrder(secKillOrder);//
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);//消息重回队列
        }
    }

}
