package com.shdr.eva.mq.rocketmq.inject;

import com.shdr.eva.mq.annotation.RocketMQListener;
import com.shdr.eva.mq.common.Message;
import org.springframework.stereotype.Service;

/**
 * spring 注入方式接收消息
 */
@Service
public class RocketConsumer {

    @RocketMQListener(topic = "testTopic", group = "testGroup")
    public void handleMessage(Message message) {
        System.out.println("🚀 [RocketMQ] 收到消息: " + message.toString());
    }
}

