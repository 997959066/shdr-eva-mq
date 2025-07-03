package com.shdr.eva.mq.exchange.fanout;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FanoutConsumer1 {

    @RabbitListener(queues = FanoutRabbitConfig.FANOUT_QUEUE_1)
    public void receive(String msg) {
        System.out.println("📢 FanoutConsumer1 收到: " + msg);
    }
}