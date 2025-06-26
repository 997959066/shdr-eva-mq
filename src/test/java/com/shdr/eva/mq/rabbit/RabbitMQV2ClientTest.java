package com.shdr.eva.mq.rabbit;

import com.shdr.eva.mq.MessageQueueClient;
import com.shdr.eva.mq.common.MessagePayload;
import com.shdr.eva.mq.v2rabbit.RabbitMQV2Client;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

/**
 * RabbitMQClient 单元测试类
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RabbitMQV2ClientTest {
    private static RabbitMQV2Client client;

    private static final String TOPIC = "test.fanout.exchange";

    @BeforeAll
    static void setup() throws IOException, TimeoutException {
        client = new RabbitMQV2Client();
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
        client.sendOne(new MessagePayload(TOPIC,msg,"1"));
    }

    //多条发送
//    @Test
//    @Order(2)
//    void testSendBatch(){
//        List<byte[]> messages = new ArrayList<>();
//        IntStream.range(1, 10).forEach(i -> {
//            String msg = "RabbitMQ 第 "+ i + "条广播消息";
//            messages.add(msg.getBytes());
//        });
//        client.sendBatch(TOPIC, messages);
//    }


    private static final String FANOUT_QUEUE = "test.fanout.queue";

    @Test
    @Order(4)
    void onMessage() throws Exception {

        client.onMessage(new MessagePayload(TOPIC,FANOUT_QUEUE), body -> {
            System.out.println("RabbitMQ 收到消息：" + body.toString());
        });
        // 保持主线程存活
        Thread.currentThread().join();
    }





}

