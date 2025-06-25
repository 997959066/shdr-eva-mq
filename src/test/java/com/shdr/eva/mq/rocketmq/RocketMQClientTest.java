package com.shdr.eva.mq.rocketmq;

import com.alibaba.fastjson.JSON;
import com.shdr.eva.mq.MessageQueueClient;
import com.shdr.eva.mq.rabbit.RabbitMQClient;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * RabbitMQClient 单元测试类
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RocketMQClientTest {

    private static RocketMQClient client;
    private static final String TOPIC = "testTopic";

    @BeforeAll
    public static void setup() throws Exception {
        client = new RocketMQClient();
    }


    @Test
    public void testSendOne() throws Exception {
        String msg = "RocketMQ 单条广播消息";
        client.sendOne(TOPIC, msg.getBytes());
    }


    @Test
    public void testSendBatch(){
        List<byte[]> messages = IntStream.range(1, 10)
                .mapToObj(i -> ("RocketMQ 第 " + i + " 条广播消息").getBytes())
                .toList();
        client.sendBatch(TOPIC, messages);
    }





    private static final String TEST_GROUP = "testGroup";
    @Test
    @Order(4)
    void onMessage() throws Exception {
        MessageQueueClient rabbit = new RocketMQClient();

        rabbit.onMessage(TOPIC, TEST_GROUP, body -> {
            System.out.println("Rocket RabbitMQ 收到消息：" + body.toString());
        });
        // 保持主线程存活
        Thread.currentThread().join();

    }


    // 拉取消息
    @Test
    public void testReceiveOne() throws Exception {
        byte[] msg = client.receiveOne(TOPIC, "*");

        if (msg == null) {
            System.out.println("Rocket No messages received");
            return;
        }
        System.out.println("Rocket Received: " + new String(msg));
    }


    @Test
    public void testReceiveBatch() throws Exception {
        List<byte[]> msgList = client.receiveBatch(TOPIC, TEST_GROUP,10);


        if (msgList.isEmpty()) {
            System.out.println("Rocket No messages received");
            return;
        }

        msgList.forEach(msg -> System.out.println("Rocket Received: " + new String(msg)));
    }

}

