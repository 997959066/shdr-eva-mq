package com.shdr.eva.mq.rabbit.exchange;

import com.shdr.eva.mq.exchange.topic.TopicProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TopicRabbitTest {

    @Autowired
    private TopicProducer producer;

    @Test
    void testTopicSendUserCreate() throws InterruptedException {
        producer.send("user.create", "创建用户的消息");
        Thread.sleep(1000);
    }

    @Test
    void testTopicSendUserUpdateProfile() throws InterruptedException {
        producer.send("user.update.profile", "更新用户信息消息");
        Thread.sleep(1000);
    }
}
