package com.shdr.eva.mq.rabbit.exchange;

import com.shdr.eva.mq.exchange.headers.HeadersProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HeadersRabbitTest {

    @Autowired
    private HeadersProducer producer;

    @Test
    void testHeadersSend() throws InterruptedException {
        producer.send("Headers 消息内容");
        Thread.sleep(1000); // 等待消费者接收
    }
}
