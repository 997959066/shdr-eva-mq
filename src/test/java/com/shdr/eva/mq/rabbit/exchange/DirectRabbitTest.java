package com.shdr.eva.mq.rabbit.exchange;

import com.shdr.eva.mq.exchange.direct.DirectProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DirectRabbitTest {

    @Autowired
    private DirectProducer producer;

    @Test
    void testDirectSend() throws InterruptedException {
        producer.send("Hello Direct MQ!");
        Thread.sleep(1000); // 等待消费者打印
    }
}