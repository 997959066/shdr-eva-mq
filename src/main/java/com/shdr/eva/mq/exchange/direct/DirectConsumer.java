package com.shdr.eva.mq.exchange.direct;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DirectConsumer {

    @RabbitListener(queues = DirectRabbitConfig.DIRECT_QUEUE)
    public void receive(String msg) {
        System.out.println("🔔 Direct 收到消息: " + msg);
    }
}
