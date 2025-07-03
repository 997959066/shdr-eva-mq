package com.shdr.eva.mq.exchange.topic;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topic exchange（主题交换机）
 * 配置时 ：exchange routingKey queue
 * 生产者指定 ：routingKey （按照路由key匹配规则，精准分发消息给对应队列）
 * 消费者指定 ：queue
 *
 * 典型场景：日志系统（路由键如 logs.error、logs.info 可以将错误日志和信息日志分别发送到不同的队列进行处理）、
 * 分级通知（路由键如 payment.success、payment.failure 可以将支付相关的消息路由到处理支付的不同队列）
 */
@Configuration
public class TopicRabbitConfig {

    public static final String TOPIC_EXCHANGE = "topic.exchange";
    public static final String TOPIC_QUEUE_1 = "topic.queue.1";
    public static final String TOPIC_QUEUE_2 = "topic.queue.2";
    public static final String ROUTING_KEY_1 = "user.*";
    public static final String ROUTING_KEY_2 = "user.#";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE_1);
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE_2);
    }

    @Bean
    public Binding binding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY_1);
    }

    @Bean
    public Binding binding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY_2);
    }
}

