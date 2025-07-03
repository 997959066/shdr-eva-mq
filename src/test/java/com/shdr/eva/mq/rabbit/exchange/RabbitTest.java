package com.shdr.eva.mq.rabbit.exchange;

import com.shdr.eva.mq.exchange.direct.DirectProducer;
import com.shdr.eva.mq.exchange.fanout.FanoutProducer;
import com.shdr.eva.mq.exchange.headers.HeadersProducer;
import com.shdr.eva.mq.exchange.topic.TopicProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RabbitTest {

    @Autowired
    private DirectProducer directProducer;
    @Autowired
    private FanoutProducer fanoutProducer;
    @Autowired
    private HeadersProducer headersProducer;
    @Autowired
    private TopicProducer topicProducer;

    //--------Direct------
    @Test
    void testDirectSend() throws InterruptedException {
        directProducer.send("Hello Direct MQ!");
        Thread.sleep(1000); // 等待消费者打印
    }

    //--------Fanout------

    @Test
    void testFanoutSend() throws InterruptedException {
        fanoutProducer.broadcast("广播消息 Fanout MQ");
        Thread.sleep(1000); // 等待两个消费者打印
    }

    //--------Headers------

    @Test
    void testHeadersSend() throws InterruptedException {
        headersProducer.send("Headers 消息内容");
        Thread.sleep(1000); // 等待消费者接收
    }

    //--------topic------
    @Test
    void testTopicSendUserCreate() throws InterruptedException {
        topicProducer.send("user.create", "创建用户的消息");
        Thread.sleep(1000);
    }

    @Test
    void testTopicSendUserUpdateProfile() throws InterruptedException {
        topicProducer.send("user.update.profile", "更新用户信息消息");
        Thread.sleep(1000);
    }
}