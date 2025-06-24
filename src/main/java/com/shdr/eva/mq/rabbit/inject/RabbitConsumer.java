package com.shdr.eva.mq.rabbit.inject;

import com.shdr.eva.mq.annotation.RabbitMQListener;
import com.shdr.eva.mq.common.Message;
import org.springframework.stereotype.Component;

/**
 * 使用代码示例
 */
@Component
public class RabbitConsumer {

    @RabbitMQListener(topic = "test.fanout.exchange", group = "test.fanout.queue")
    public void handleMessage(Message message) {
        System.out.println("✅ 收到消息: " + message.toString());
    }
}

