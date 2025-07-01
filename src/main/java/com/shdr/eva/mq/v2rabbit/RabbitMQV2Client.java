package com.shdr.eva.mq.v2rabbit;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import com.shdr.eva.mq.MessageClient;
import com.shdr.eva.mq.common.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * RabbitMQ 实现，支持基础发送接收、topic与fanout发布订阅、批量与缓存发送
 * RabbitMQ
 */
@Component
@Slf4j
public class RabbitMQV2Client implements MessageClient {
    private Connection connection; // 与 RabbitMQ 的连接对象
    private Channel channel;       // 通信信道

    /**
     * 构造函数：通过明确定义参数的方式连接 RabbitMQ（适用于 localhost 环境）
     */
    public RabbitMQV2Client() throws IOException, TimeoutException {
        log.info("Initializing RabbitMQClient with explicit parameters...");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");       // 设置主机地址为本地
        factory.setPort(5672);              // 设置端口（RabbitMQ 默认 AMQP 端口）
        factory.setUsername("guest");      // 设置用户名（默认用户）
        factory.setPassword("guest");      // 设置密码

        this.connection = factory.newConnection();   // 建立连接
        this.channel = connection.createChannel();   // 创建通信信道
        this.channel.confirmSelect();                // 启用发布确认机制
        log.info("RabbitMQ connection established.");
    }


    /**
     * rabbitmq fanout 模式 忽略路由key
     *
     * @param message 消息内容（字节数组）
     *                RabbitMQ需要开启 Confirm 模式，才有返回。
     */
    @Override
    public void sendOne(Message message) {
        String topic = message.getTopic();
        String msgId = UUID.randomUUID().toString();
        Object body = message.getBody();
        String msgBody = JSON.toJSONString(body);
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().messageId(msgId).build();
        log.info("sendOne Publishing to message={} ", JSON.toJSONString(message));
        try {
            channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true); // 声明交换机
            channel.basicPublish(topic, "", props, msgBody.getBytes()); // 发送消息
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * rabbitmq自身不支持批量，需要手动实现
     * <p>
     * [{},{}]
     */
    @Override
    public void sendBatch(List<Message> messageList) {
        try {
            for (Message message : messageList) {
                String topic = message.getTopic();
                String msgId = UUID.randomUUID().toString();
                Object body = message.getBody();
                String msgBody = JSON.toJSONString(body);
                AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                        .messageId(msgId).build();
                channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true); // 声明交换机
                channel.basicPublish(topic, "", props, msgBody.getBytes()); // 发送消息
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    /**
     * RabbitMQ 默认没有消息 ID（Message ID）
     * 如果你想要 消息ID，需要在发送消息时显式设置 messageId
     *
     * @param topic
     * @param group
     * @param callback
     * @throws Exception
     */
    @Override
    public void onMessage(String topic,String group, Consumer<Message> callback) {
        // 声明 fanout 类型交换机
        try {
            channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true);
            // 声明并绑定队列（这里 queue 就是 group）
            channel.queueDeclare(group, true, false, false, null);
            channel.queueBind(group, topic, "");
            // 定义消费者
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                byte[] body = delivery.getBody();
                String messageId = delivery.getProperties().getMessageId();
                // 构造自定义 Message 对象
                Message msg = new Message(topic,group,new String(body), messageId); // messageId暂时传null或从消息属性获取
//            log.info("📨 Received message from RabbitMQ: {}", new String(body));
                callback.accept(msg);
            };
            // 开始消费
            channel.basicConsume(group, true, deliverCallback, consumerTag -> {
                log.warn("❌ Consumer cancelled: {}", consumerTag);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }









    /**
     * 关闭资源
     */
    public void close() {
        if (channel != null && channel.isOpen()) {
            try {
                log.info("Closing RabbitMQ channel.");
                channel.close();
            } catch (Exception ignored) {
            }
        }
        if (connection != null && connection.isOpen()) {
            try {
                log.info("Closing RabbitMQ connection.");
                connection.close();
            } catch (Exception ignored) {
            }
        }
    }

}
