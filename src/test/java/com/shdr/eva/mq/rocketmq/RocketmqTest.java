package com.shdr.eva.mq.rocketmq;

import com.shdr.eva.mq.rabbit.inject.RabbitProducer;
import com.shdr.eva.mq.rocketmq.inject.RocketProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RocketmqTest {

    @Autowired
    private RocketProducer rocketProducer;

    @Test
    void sendOne() {
        rocketProducer.sendOne();
    }

    @Test
    void sendBatch() {
        rocketProducer.sendBatch();
    }
}
