package com.shdr.eva.mq.rocketmq;

import com.shdr.eva.mq.rabbit.RabbitMQClient;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * RabbitMQClient 单元测试类
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RocketMQClientTest {

    private static RocketMQClient client;
    private static final String TEST_TOPIC = "testTopic";

    @BeforeAll
    public static void setup() throws Exception {
        client = new RocketMQClient();
    }

    @AfterAll
    public static void tearDown() {
        // RocketMQ producer 无需关闭（可选）
    }

    @Test
    public void testSendOne() throws Exception {
        String message = " RocketMQ 发送单条广播消息!";
        client.sendOne(TEST_TOPIC, message.getBytes());
    }

    @Test
    public void testReceiveOne() throws Exception {
        byte[] msg = client.receiveOne(TEST_TOPIC, "*");

        if (msg == null) {
            System.out.println("No messages received");
            return;
        }
        System.out.println("Received: " + new String(msg));
    }

    @Test
    public void testSendBatch() throws Exception {
        List<byte[]> messages = Arrays.asList(
                "SendBatch msg1".getBytes(),
                "SendBatch msg2".getBytes(),
                "SendBatch msg3".getBytes()
        );
        client.sendBatch(TEST_TOPIC, messages);
    }

    @Test
    public void testReceiveBatch() throws Exception {
        List<byte[]> msgList = client.receiveBatch(TEST_TOPIC, "group",10);


        if (msgList.isEmpty()) {
            System.out.println("No messages received");
            return;
        }

        msgList.forEach(msg -> System.out.println("Received: " + new String(msg)));
    }




}

