package com.shdr.eva.mq.rocketmq.inject;

import com.shdr.eva.mq.rabbit.RabbitMQClient;
import com.shdr.eva.mq.rocketmq.RocketMQClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * spring 注入方式接收消息
 */
@Service
public class RocketConsumer {

    @Autowired
    private RocketMQClient rocketMQClient;

    private static final String TEST_TOPIC = "testTopic";
    private static final String TEST_GROUP = "testGroup";


    @PostConstruct
    public void init() throws Exception {
        rocketMQClient.onMessage(TEST_TOPIC, TEST_GROUP, message -> {
            System.out.println("Received "  + message.toString());
        });
    }
}

