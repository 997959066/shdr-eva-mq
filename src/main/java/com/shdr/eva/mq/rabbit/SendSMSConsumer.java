package com.shdr.eva.mq.rabbit;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

/**
 * @author 喵帕斯
 * @version 2.0.1
 * @date 2021/6/7 15:30
 */
@Configuration
@Slf4j
public class SendSMSConsumer {


    @RabbitListener(queues = "send.sms.mo.service.queue")
    public void process(Message message, Channel channel) {
        log.info("[{}][{}]message headers:{},message body:{}", "", "SendSMSConsumer",
                message.getMessageProperties().getHeaders(), new String(message.getBody()));
        byte[] entityBodyByte = message.getBody();
        if (entityBodyByte.length == 0) {
            log.info("[{}]", "message body is empty");
            return;
        }
    }

}
