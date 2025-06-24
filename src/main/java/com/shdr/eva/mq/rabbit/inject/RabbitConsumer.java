package com.shdr.eva.mq.rabbit.inject;

import com.shdr.eva.mq.rabbit.RabbitMQClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * spring 注入方式接收消息
 */
@Service
public class RabbitConsumer {

    @Autowired
    private  RabbitMQClient rabbitMQClient;


    private static final String FANOUT_QUEUE = "test.fanout.queue";
    private static final String TOPIC_EXCHANGE = "test.fanout.exchange";


    @PostConstruct
    public void init() throws Exception {
        rabbitMQClient.onMessage(TOPIC_EXCHANGE, FANOUT_QUEUE, message -> {
            System.out.println("Received "  + message.toString());
        });
    }
}

