package com.shdr.eva.mq.exchange.fanout;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FanoutProducer {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void broadcast(String msg) {
        amqpTemplate.convertAndSend(FanoutRabbitConfig.FANOUT_EXCHANGE, "", msg);
    }
}
