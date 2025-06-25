package com.shdr.eva.mq.rocketmq.inject;

import com.shdr.eva.mq.rabbit.RabbitMQClient;
import com.shdr.eva.mq.rocketmq.RocketMQClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class RocketProducer {
    private static final String TOPIC = "testTopic";

    @Autowired
    private RocketMQClient rocketMQClient;


    public void sendOne()  {
        String msg = "RocketMQ 单条广播消息";
        rocketMQClient.sendOne(TOPIC, msg.getBytes());
    }

    //多条发送
    public void sendBatch() {
        List<byte[]> messages = IntStream.range(1, 10)
                .mapToObj(i -> ("RocketMQ 第 " + i + " 条广播消息").getBytes())
                .toList();
        rocketMQClient.sendBatch(TOPIC, messages);
    }
}
