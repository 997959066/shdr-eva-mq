package com.shdr.eva.mq.rabbit;


import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;

@SpringBootTest
@ActiveProfiles("debug")
class RabbitmqTest {

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Test
    public void testSendMessage() {

        String proMessage = "";
        Message msg = MessageBuilder.withBody(proMessage.getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding("utf-8")
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
                    amqpTemplate.convertAndSend("mo.service.exchange.dev_01",
                            "mo.service.exchange.dev_01",
                            msg);
                }


}
