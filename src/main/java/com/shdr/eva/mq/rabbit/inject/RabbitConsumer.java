package com.shdr.eva.mq.rabbit.inject;

import com.shdr.eva.mq.annotation.MQListener;
import com.shdr.eva.mq.common.Message;
import org.springframework.stereotype.Component;

/**
 * 使用代码示例
 */
@Component
public class RabbitConsumer {

    @MQListener(topic = "test.fanout.exchange", group = "test.fanout.queue")
    public void handleMessage(Message messageOne) {
        System.out.println("✅ [RabbitMQ Consumer] 收到消息:" + messageOne.toString());
    }
}

