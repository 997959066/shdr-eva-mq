package com.shdr.eva.mq.rabbit.exchange;

import com.shdr.eva.mq.exchange.fanout.FanoutProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FanoutRabbitTest {

    @Autowired
    private FanoutProducer producer;

    @Test
    void testFanoutSend() throws InterruptedException {
        producer.broadcast("广播消息 Fanout MQ");
        Thread.sleep(1000); // 等待两个消费者打印
    }
}
