package com.shdr.eva.mq.exchange.topic;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TopicConsumer1 {

    @RabbitListener(queues = TopicRabbitConfig.TOPIC_QUEUE_1)
    public void receive(String msg) {
        System.out.println("📬 TopicConsumer1 收到: " + msg);
    }
}


