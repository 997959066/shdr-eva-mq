package com.shdr.eva.mq.rabbit;

import com.alibaba.fastjson.JSON;
import com.shdr.eva.mq.annotation.MQListener;
import com.shdr.eva.mq.common.Message;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 使用代码示例
 */
@Component
public class RabbitConsumer {

    //单条使用方法
    @MQListener(topic = "test.topic", group = "test.group")
    public void handleMessage(Message message) {
        System.out.println("✅ [Consumer Single message] 单条消息:" + JSON.toJSONString(message));
    }


    //批量使用方法
    @MQListener(topic = "test.topic.batch", group = "test.group.batch", batchSize = 5, intervalMs = 1000)
    public void handleBatch(List<Message> messages) {
        for (Message msg : messages) {
            System.out.println("✅ [Consumer Batch message] 批量消息：" + JSON.toJSONString(msg));
        }
    }
}

