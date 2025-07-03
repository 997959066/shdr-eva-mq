package com.shdr.eva.mq.rabbit;

import com.alibaba.fastjson.JSON;
import com.shdr.eva.mq.common.Message;
import com.shdr.eva.mq.common.User;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQClient 单元测试类
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RabbitMQClientTest {
    private static RabbitMQClient client;

    @BeforeAll
    static void setup() throws IOException, TimeoutException {
        client = new RabbitMQClient();
    }
    @AfterAll
    static void teardown() throws IOException, TimeoutException {
        client.close();
    }





    // 发送单条消息
    @Test
    void testSendOne(){

        User user = new User(1,"王1");

        client.sendOne(new Message("test.topic",user));
    }



    //发送多条消息
    @Test
    void testSendBatch(){
        //消息body,支持多种数据类型

        User user1 = new User(3,"张3");
        Integer in2 = 9527;
        String str3 = "我是一个字符串";

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message("test.topic",user1));
        messageList.add(new Message("test.topic",in2));
        messageList.add(new Message("test.topic",str3));

        client.sendBatch(messageList);
    }


    //监听消息
    @Test
    void testOnMessage() throws Exception {

        client.onMessage(
                "test.topic.delay",
                "test.group",
                message -> System.out.println("✅ onMessage 收到消息 : "+ JSON.toJSONString(message))
        );

        Thread.currentThread().join();
    }


    //测试注入使用方式，批量监听消息
    @Test
    void testOnBatchMessage() throws Exception {

        client.onBatchMessage(
                "test.topic.batch",
                "test.group.batch",
                5, // 每5条触发一次
                1000,       // 等待时间毫秒 MILLISECONDS
                batchMessage -> {
                    batchMessage.forEach(msg -> System.out.println("📩 onBatchMessage  " + JSON.toJSONString(msg)));
                }
        );

        Thread.currentThread().join();
    }




    //发送多条消息
    @Test
    void testSendBatchMsg(){

        User user1 = new User(2,"刘2");
        User user2 = new User(3,"张3");
        User user3 = new User(4,"赵4");

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message("test.topic.batch",user1));
        messageList.add(new Message("test.topic.batch",user2));
        messageList.add(new Message("test.topic.batch",user3));

        client.sendBatch(messageList);
    }





    // 测试延迟消息
    @Test
    void testSendDelay() throws IOException {

        User user = new User(1,"王1");
        client.sendDelay(new Message("test.topic.delay1",user),10000);

    }

    //监听消息
    @Test
    void testDelayOnMessage() throws Exception {

        client.onMessageDelay(
                "test.topic.delay1",
                "test.group.delay1",
                message -> System.out.println("✅ onMessageDelay 收到消息 : "+ JSON.toJSONString(message))
        );

        Thread.currentThread().join();
    }
}

