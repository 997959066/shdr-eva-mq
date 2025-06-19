package com.shdr.eva.mq.rabbit;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.GetResponse;
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
    private static final String QUEUE = "test.queue";
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
    @Order(1)
    void testSendAndReceiveOne() throws Exception {
        String message = "Hello MQ";
        client.sendOne(QUEUE, message.getBytes());
        byte[] received = client.receiveOne(QUEUE);
        Assertions.assertNotNull(received);
        Assertions.assertEquals(message, new String(received));
    }

    @Test
    @Order(2)
    void testSendBatchAndReceiveBatch() throws Exception {
        List<byte[]> messages = Arrays.asList("A".getBytes(), "B".getBytes(), "C".getBytes());
        client.sendBatch(QUEUE, messages);
        List<byte[]> received = client.receiveBatch(QUEUE, 10);
        Assertions.assertTrue(received.size() >= 3);
    }

    @Test
    @Order(3)
    void testStoreAndResend() throws Exception {
        client.storeMessage(QUEUE, "Cached Message".getBytes());
        client.sendStoredMessages();
        byte[] received = client.receiveOne(QUEUE);
        Assertions.assertNotNull(received);
        Assertions.assertEquals("Cached Message", new String(received));
    }

    @Test
    @Order(4)
    void testPublishSubscribeTopic() throws Exception {
        client.publishTopic(TOPIC_EXCHANGE, "user.signup", "Topic Message".getBytes());
        List<byte[]> received = client.subscribeTopic(TOPIC_EXCHANGE, "user.#");
        Assertions.assertFalse(received.isEmpty());
    }

    @Test
    @Order(5)
    void testPublishSubscribeFanout() throws Exception {
        // 🟡 先绑定一个临时队列（模拟订阅）
        String exchange = FANOUT_EXCHANGE;
        client.getChannel().exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true);
        String queue = client.getChannel().queueDeclare().getQueue(); // 创建一个临时队列
        client.getChannel().queueBind(queue, exchange, ""); // 绑定队列到 fanout 交换机

        // 🟢 然后发布消息
        client.publishFanout(exchange, "Fanout Message".getBytes());

        // 🔵 然后消费消息
        List<byte[]> msgs = new ArrayList<>();
        while (true) {
            GetResponse resp = client.getChannel().basicGet(queue, true);
            if (resp == null) break;
            msgs.add(resp.getBody());
        }

        // 输出
        for (byte[] msg : msgs) {
            System.out.println("Received: " + new String(msg));
        }

        Assertions.assertFalse(msgs.isEmpty(), "Expected message in fanout queue");  }

}

