package com.shdr.eva.mq.rabbit.inject;

import com.alibaba.fastjson.JSON;
import com.shdr.eva.mq.annotation.MQListener;
import com.shdr.eva.mq.common.Message;
import org.springframework.stereotype.Component;

/**
 * 使用代码示例
 */
@Component
public class RabbitConsumer {

    @MQListener(topic = "test.topic", group = "test.group")
    public void handleMessage(Message message) {
        System.out.println("✅ [RabbitMQ Consumer] 收到消息:" + JSON.toJSONString(message));
    }

}

