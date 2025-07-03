package com.shdr.eva.mq.exchange.headers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class HeadersConsumer {

    @RabbitListener(queues = HeadersRabbitConfig.HEADERS_QUEUE)
    public void receive(String msg) {
        System.out.println("📩 HeadersConsumer 收到: " + msg);
    }
}
