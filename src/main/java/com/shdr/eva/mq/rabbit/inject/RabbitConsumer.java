package com.shdr.eva.mq.rabbit.inject;

import com.shdr.eva.mq.annotation.RabbitMQListener;
import com.shdr.eva.mq.common.MessageOne;
import org.springframework.stereotype.Component;

/**
 * 使用代码示例
 */
@Component
public class RabbitConsumer {

    @RabbitMQListener(topic = "test.fanout.exchange", group = "test.fanout.queue")
    public void handleMessage(MessageOne messageOne) {
        System.out.println("✅ [RabbitMQ] 收到消息:" + messageOne.toString());
    }
}

