package com.shdr.eva.mq.exchange.headers;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Headers exchange（头交换机）
 * 基于 headers 键值匹配，routingKey 不参与路由。
 * 配置时 ：exchange  queue
 * 生产者指定 ：exchange
 * 消费者指定 ：queue
 *
 * 典型场景 ：
 * 适用于多条件过滤（在订单处理系统中，消息可以包含 paymentMethod 和 region 属性，根据这两个属性路由到不同的处理队列。）
 * 复杂业务逻辑（根据用户的 role 和 status 处理消息，确保不同角色的用户满足不同的处理逻辑）
 */
@Configuration
public class HeadersRabbitConfig {

    public static final String HEADERS_EXCHANGE = "headers.exchange";
    public static final String HEADERS_QUEUE = "headers.queue";

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Queue headersQueue() {
        return new Queue(HEADERS_QUEUE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(headersQueue()).to(headersExchange())
                .whereAll("type", "format").exist();
    }
}
