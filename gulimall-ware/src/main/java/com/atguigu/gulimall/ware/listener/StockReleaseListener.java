package com.atguigu.gulimall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.mq.OrderEntityTo;
import com.atguigu.common.to.mq.StockDetailTo;
import com.atguigu.common.to.mq.StockLockedTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.feign.OrderFeignService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.OrderEntityVo;
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
 * @className StockReleaseListener
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/10 13:38
 */
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {
    @Autowired
    WareSkuService wareSkuService;

    /**
     * 库存自动解锁：发生异常、未支付过期等
     * @param to
     * @param message
     */
    @RabbitHandler
    public void handleStockLockRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        System.out.println("库存工作服务收到解锁库存的消息");
        try {
            wareSkuService.unLockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//消息消费成功，单个回复确认
        } catch (Exception e) {//有任何异常，都说明消息消费失败，让消息重新归队
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);//消息消费失败，放回队列
        }
    }

    /**
     * 库存自动解锁：订单服务自动发出一个解锁库存的消息（防止订单服务延时，订单状态不对，造成库存服务解锁不了）
     * @param to
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void handleOrderClosedRelease(OrderEntityTo to, Message message, Channel channel) throws IOException {
        System.out.println("订单关闭，准备解锁库存");
        try {
            wareSkuService.unLockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//消息消费成功，单个回复确认
        } catch (Exception e) {//有任何异常，都说明消息消费失败，让消息重新归队
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);//消息消费失败，放回队列
        }
    }
}
