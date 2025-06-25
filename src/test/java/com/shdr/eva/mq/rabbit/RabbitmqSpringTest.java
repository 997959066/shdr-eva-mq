package com.shdr.eva.mq.rabbit;

import com.shdr.eva.mq.rabbit.inject.RabbitProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RabbitmqSpringTest {

    @Autowired
    private RabbitProducer rabbitProducer;

    @Test
    void sendOne() {
        rabbitProducer.sendOne();
    }

    @Test
    void sendBatch() {
        rabbitProducer.sendBatch();
    }
}
