package com.shdr.eva.mq.exchange.headers;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



import java.nio.charset.StandardCharsets;

@Component
public class HeadersProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String msg) {
        MessageProperties properties = new MessageProperties();
        properties.setHeader("type", "pdf");
        properties.setHeader("format", "A4");
        Message message = new Message(msg.getBytes(StandardCharsets.UTF_8), properties);

        rabbitTemplate.send(HeadersRabbitConfig.HEADERS_EXCHANGE, "", message);
    }
}
