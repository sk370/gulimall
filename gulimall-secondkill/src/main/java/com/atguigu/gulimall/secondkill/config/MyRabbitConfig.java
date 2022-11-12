package com.atguigu.gulimall.secondkill.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 指定rabbitmq数据序列化机制，以及定制RabbitTemplate
 * @author zhuyuqi
 * @version v0.0.1
 * @className MyRabbitConfig
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/07 11:40
 */
@Configuration
//@EnableRabbit//开启rabbitmq功能(监听消息rabbitmqlistener才需要，只是放消息可以不加)
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
//
//    /**
//     * 定制RabbitTemplate
//     */
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
//                // TODO 服务器borker收到消息，将消息写入数据库`mq_message`
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
//                // TODO 消息没有抵达队列，修改数据库中当前消息的错误状态`mq_message`
//            }
//        });
//    }
}
