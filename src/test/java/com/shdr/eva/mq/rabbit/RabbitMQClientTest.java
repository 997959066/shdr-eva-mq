package com.shdr.eva.mq.rabbit;

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
    private static final String TOPIC_EXCHANGE = "test.topic.exchange";
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
    @Order(100)
    void testSendAndReceiveOne() throws Exception {
        // 🟡 先绑定一个临时队列（模拟订阅）
        String exchange = FANOUT_EXCHANGE;
        String queue = FANOUT_QUEUE;
        // 🟢 然后发布单条消息
        client.sendOne(exchange, "单条系统广播消息 Fanout Message".getBytes());
        // 🔵 然后消费单条消息
        byte[] msg = client.receiveOne(exchange, queue);

        if (msg == null) {
            System.out.println("No messages received");
            return;
        }
        System.out.println("Received: " + new String(msg));

    }


    @Test
    @Order(2)
    void testSendBatch() throws Exception {
        String exchange = FANOUT_EXCHANGE;

        List<byte[]> messages = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String s = i + "__系统广播消息 Fanout Message";
            messages.add(s.getBytes());
        }

        // 🟢 然后发多条消息
        client.sendBatch(exchange, messages);

    }


    @Test
    @Order(3)
    void testReceiveBatch() throws Exception {
        String exchange = FANOUT_EXCHANGE;
        String queue = FANOUT_QUEUE;

        // 🔵 然后消费多条消息
        List<byte[]> msgList = client.receiveBatch(exchange, queue, 10);

        if (msgList.isEmpty()) {
            System.out.println("No messages received");
            return;
        }

        msgList.forEach(msg -> System.out.println("Received: " + new String(msg)));

    }


//    @Test
//    @Order(5)
//    void testPublishSubscribeFanout() throws Exception {
//        // 🟡 先绑定一个临时队列（模拟订阅）
//        String exchange = FANOUT_EXCHANGE;
//        client.getChannel().exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true);
//        client.getChannel().queueDeclare(FANOUT_QUEUE, true, false, false, null); // 自定义队列
//        client.getChannel().queueBind(FANOUT_QUEUE, exchange, ""); // 绑定队列到 fanout 交换机
//
//        // 🟢 然后发布消息
//        client.publishFanout(exchange, "Fanout Message".getBytes());
//
//        // 🔵 然后消费消息
//        List<byte[]> msgs = new ArrayList<>();
//        while (true) {
//            GetResponse resp = client.getChannel().basicGet(FANOUT_QUEUE, true);
//            if (resp == null) break;
//            msgs.add(resp.getBody());
//        }
//
//        // 输出
//        for (byte[] msg : msgs) {
//            System.out.println("Received: " + new String(msg));
//        }
//
//        Assertions.assertFalse(msgs.isEmpty(), "Expected message in fanout queue");
//    }


}

