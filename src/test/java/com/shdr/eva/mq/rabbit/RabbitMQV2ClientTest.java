package com.shdr.eva.mq.rabbit;

import com.shdr.eva.mq.common.Message;
import com.shdr.eva.mq.v2rabbit.RabbitMQV2Client;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

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
        client.sendOne(new Message<String> (TOPIC,msg,"1"));
    }

    //多条发送
    @Test
    @Order(2)
    void testSendBatch(){

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message<String>(TOPIC,"RabbitMQ 第 1 条广播消息","1"));
        messageList.add(new Message<String>(TOPIC,"RabbitMQ 第 2 条广播消息","1"));
        client.sendBatch(messageList);
    }


    private static final String FANOUT_QUEUE = "test.fanout.queue";

    @Test
    @Order(4)
    void onMessage() throws Exception {

        client.onMessage(TOPIC, FANOUT_QUEUE, msg -> {
            System.out.println("✅ 收到消息："+msg.toString());
        });
        // 保持主线程存活
        Thread.currentThread().join();
    }





}

