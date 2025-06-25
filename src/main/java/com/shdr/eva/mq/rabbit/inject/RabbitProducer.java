package com.shdr.eva.mq.rabbit.inject;

import com.shdr.eva.mq.rabbit.RabbitMQClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class RabbitProducer {
    private static final String TOPIC = "test.fanout.exchange";

    @Autowired
    private RabbitMQClient rabbitMQClient;


    public void sendOne()  {
        String msg = "RabbitMQ 单条广播消息";
        rabbitMQClient.sendOne(TOPIC, msg.getBytes());
    }

    //多条发送
    public void sendBatch() {
        List<byte[]> messages = IntStream.range(1, 10)
                .mapToObj(i -> ("RabbitMQ 第 " + i + " 条广播消息").getBytes())
                .toList();
        rabbitMQClient.sendBatch(TOPIC, messages);
    }
}
