package com.atguigu.gulimall.ware.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.Channel;

/**
 * 指定rabbitmq数据序列化机制，以及定制RabbitTemplate
 * @author zhuyuqi
 * @version v0.0.1
 * @className MyRabbitConfig
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/07 11:40
 */
@Configuration
public class MyRabbitConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 指定序列化规则
     * @return
     */
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制RabbitTemplate
     */
//    @PostConstruct//对象创建完成（调用构造器以后），执行这个方法
//    public void initRabbitTempate(){
//        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//            /**
//             * 只要消息抵达Broker就ack=true
//             * @param correlationData 当前消息的唯一关联数据（消息的唯一id）
//             * @param ack Broker是否成功收到消息
//             * @param cause 失败的原因
//             */
//            @Override
//            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//            }
//        });
//
//        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
//            /**
//             *  只要消息没有投递给指定的队列，就触发这个失败回调
//             * @param message   投递失败的消息的详细信息
//             * @param replyCode 回复的状态码
//             * @param replyText 回复的文本内容
//             * @param exchange  当时消息是发给哪个交换机的
//             * @param routingKey    当时消息使用的哪个路由键
//             */
//            @Override
//            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
//            }
//        });
//    }

    /**
     * 库存延时队列（死信队列）
     * @return
     */
    @Bean
    public Queue stockDelayQueue(){
        Map<String, Object> arguments = new HashMap<>();
        //设置死信队列参数
        arguments.put("x-dead-letter-exchange","stock-event-exchange");
        arguments.put("x-dead-letter-routing-key","stock.release");
        arguments.put("x-message-ttl",120000);//设置了2分钟过期，真实业务可设置为40分钟
        return new Queue("stock.delay.queue", true, false, false,arguments);
    }

    /**
     * 到期消息的消费队列
     * @return
     */
    @Bean
    public Queue stockReleaseStockQueue(){
        return new Queue("stock.release.stock.queue", true,false,false);
    }

    /**
     * 死信交换机兼到期消息处置交换机
     * @return
     */
    @Bean
    public Exchange stockEventExchange(){
        return new TopicExchange("stock-event-exchange",true,false);
    }

    /**
     * 死信队列和交换机的绑定
     * @return
     */
    @Bean
    public Binding stockReleaseBinding(){
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,"stock-event-exchange","stock.release.#",null);
    }

    /**
     * 消费队列与交换机的绑定
     * @return
     */
    @Bean
    public Binding stockLockedBinding(){
        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE,"stock-event-exchange","stock.locked",null);
    }
}
