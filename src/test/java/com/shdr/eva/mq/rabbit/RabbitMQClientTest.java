package com.shdr.eva.mq.rabbit;

import com.alibaba.fastjson.JSON;
import com.shdr.eva.mq.MessageQueueClient;
import com.shdr.eva.mq.rabbit.inject.RabbitProducer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

/**
 * RabbitMQClient 单元测试类
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RabbitMQClientTest {
    private static RabbitMQClient client;

    private static final String TOPIC = "test.fanout.exchange";

    @BeforeAll
    static void setup() throws IOException, TimeoutException {
        client = new RabbitMQClient();
    }
    @AfterAll
    static void teardown() throws IOException, TimeoutException {
        client.close();
    }

    //单条消息
    @Test
    @Order(1)
    void testSendOne()  {
        String msg = "RabbitMQ 单条广播消息";
        client.sendOne(TOPIC, msg.getBytes());
    }

    //多条发送
    @Test
    @Order(2)
    void testSendBatch(){
        List<byte[]> messages = new ArrayList<>();
        IntStream.range(1, 10).forEach(i -> {
            String msg = "RabbitMQ 第 "+ i + "条广播消息";
            messages.add(msg.getBytes());
        });
        client.sendBatch(TOPIC, messages);
    }


    private static final String FANOUT_QUEUE = "test.fanout.queue";

    @Test
    @Order(4)
    void onMessage() throws Exception {
        MessageQueueClient rabbit = new RabbitMQClient();

        rabbit.onMessage(TOPIC, FANOUT_QUEUE, body -> {
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
        String exchange = TOPIC;
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

