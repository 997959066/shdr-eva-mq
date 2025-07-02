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


    //单条消息
    @Test
    @Order(1)
    void testSendOne()  {
        User user = new User(1,"zhang3",19);

        client.sendOne(new Message<User> ("test.topic",user));
    }

    //多条发送
    @Test
    @Order(2)
    void testSendBatch(){
        User user1 = new User(2,"wang2",16);
        User user2 = new User(3,"zhang3",12);

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message<User>("test.topic",user1));
        messageList.add(new Message<User>("test.topic",user2));

        client.sendBatch(messageList);
    }



    @Test
    @Order(4)
    void onMessage() throws Exception {

        client.onMessage("test.topic", "test.group", msg -> {
            System.out.println("✅ onMessage 收到消息 : "+ JSON.toJSONString(msg));
        });
        // 保持主线程存活
        Thread.currentThread().join();

    }





}

