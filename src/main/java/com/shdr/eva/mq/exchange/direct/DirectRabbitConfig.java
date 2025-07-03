package com.shdr.eva.mq.exchange.direct;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Direct exchange（直连交换机）
 * 典型场景：精准投递、点对点，routingKey 完全匹配
 * 配置时 ：exchange routingKey queue
 * 生产者指定 ：exchange + routingKey
 * 消费者指定 ：queue
 */
@Configuration
public class DirectRabbitConfig {

    public static final String DIRECT_QUEUE = "direct.queue";
    public static final String DIRECT_EXCHANGE = "direct.exchange";
    public static final String DIRECT_ROUTING_KEY = "direct.key";

    //创建队列
    @Bean
    public Queue directQueue() {
        return new Queue(DIRECT_QUEUE);
    }

    // 创建 Direct 类型的交换机
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    //绑定队列和交换机，并指定 bindingKey
    @Bean
    public Binding directBinding() {
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(DIRECT_ROUTING_KEY);
    }
}
