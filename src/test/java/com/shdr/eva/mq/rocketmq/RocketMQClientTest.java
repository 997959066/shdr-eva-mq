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

/**
 * RabbitMQClient 单元测试类
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RocketMQClientTest {

    private static RocketMQClient client;
    private static final String TEST_TOPIC = "testTopic";
    private static final String TEST_GROUP = "testGroup";

    @BeforeAll
    public static void setup() throws Exception {
        client = new RocketMQClient();
    }

    @Test
    public void testSendOne() throws Exception {
        String message = " RocketMQ 发送单条广播消息!";
        client.sendOne(TEST_TOPIC, message.getBytes());
    }



    @Test
    public void testSendBatch() throws Exception {
        List<byte[]> messages = Arrays.asList(
                "Rocket SendBatch msg1".getBytes(),
                "Rocket SendBatch msg2".getBytes(),
                "Rocket SendBatch msg3".getBytes()
        );
        client.sendBatch(TEST_TOPIC, messages);
    }

    @Test
    @Order(4)
    void onMessage() throws Exception {
        MessageQueueClient rabbit = new RocketMQClient();

        rabbit.onMessage(TEST_TOPIC, TEST_GROUP, body -> {
            System.out.println("Rocket RabbitMQ 收到消息：" + body.toString());
        });
        // 保持主线程存活
        Thread.currentThread().join();

    }


    // 拉取消息
    @Test
    public void testReceiveOne() throws Exception {
        byte[] msg = client.receiveOne(TEST_TOPIC, "*");

        if (msg == null) {
            System.out.println("Rocket No messages received");
            return;
        }
        System.out.println("Rocket Received: " + new String(msg));
    }


    @Test
    public void testReceiveBatch() throws Exception {
        List<byte[]> msgList = client.receiveBatch(TEST_TOPIC, TEST_GROUP,10);


        if (msgList.isEmpty()) {
            System.out.println("Rocket No messages received");
            return;
        }

        msgList.forEach(msg -> System.out.println("Rocket Received: " + new String(msg)));
    }

}

