package com.shdr.eva.mq.exchange.fanout;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FanoutConsumer2 {

    @RabbitListener(queues = FanoutRabbitConfig.FANOUT_QUEUE_2)
    public void receive(String msg) {
        System.out.println("📢 FanoutConsumer2 收到: " + msg);
    }
}