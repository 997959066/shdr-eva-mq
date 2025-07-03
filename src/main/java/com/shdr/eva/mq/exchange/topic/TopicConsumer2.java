package com.shdr.eva.mq.exchange.topic;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TopicConsumer2 {

    @RabbitListener(queues = TopicRabbitConfig.TOPIC_QUEUE_2)
    public void receive(String msg) {
        System.out.println("📬 TopicConsumer2 收到: " + msg);
    }
}