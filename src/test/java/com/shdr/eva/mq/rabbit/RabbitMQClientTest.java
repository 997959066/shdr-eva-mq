package com.shdr.eva.mq.rabbit;

import com.alibaba.fastjson.JSON;
import com.shdr.eva.mq.MessageQueueClient;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQClient 单元测试类
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RabbitMQClientTest {

    private static RabbitMQClient client;
    private static final String FANOUT_QUEUE = "test.fanout.queue";
    private static final String FANOUT_EXCHANGE = "test.fanout.exchange";

    @BeforeAll
    static void setup() throws IOException, TimeoutException {
        client = new RabbitMQClient();
    }

    @AfterAll
    static void teardown() throws IOException, TimeoutException {
        client.close();
    }

    @Test
    @Order(1)
    void testSendAndReceiveOne() throws Exception {
        client.sendOne(FANOUT_EXCHANGE, "RabbitMQ 单条系统广播消息 Fanout Message".getBytes());
    }

    /**
     * 多条发送
     * @throws Exception
     */
    @Test
    @Order(2)
    void testSendBatch() throws Exception {
        List<byte[]> messages = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String s = i + "_RabbitMQ__系统广播消息 Fanout Message";
            messages.add(s.getBytes());
        }
        client.sendBatch(FANOUT_EXCHANGE, messages);
    }


    @Test
    @Order(4)
    void onMessage() throws Exception {
        MessageQueueClient rabbit = new RabbitMQClient();

        rabbit.onMessage(FANOUT_EXCHANGE, FANOUT_QUEUE, body -> {
            System.out.println("RabbitMQ 收到消息：" + body.toString());
        });
        // 保持主线程存活
        Thread.currentThread().join();

    }



    /**
     * 多条接收
     * @throws Exception
     */
    @Test
    @Order(3)
    void testReceiveBatch() throws Exception {
        String exchange = FANOUT_EXCHANGE;
        String queue = FANOUT_QUEUE;

        // 🔵 然后消费多条消息
        List<byte[]> msgList = client.receiveBatch(exchange, queue, 10);

        if (msgList.isEmpty()) {
            System.out.println("RabbitMQ No messages received");
            return;
        }

        msgList.forEach(msg -> System.out.println("RabbitMQ Received: " + new String(msg)));

    }




}

