package com.shdr.eva.mq.exchange.topic;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopicProducer {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(String routingKey, String msg) {
        amqpTemplate.convertAndSend(TopicRabbitConfig.TOPIC_EXCHANGE, routingKey, msg);
    }
}
