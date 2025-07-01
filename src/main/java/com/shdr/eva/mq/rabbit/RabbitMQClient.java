package com.shdr.eva.mq.rabbit;

import com.rabbitmq.client.*;
import com.shdr.eva.mq.MessageQueueClient;
import com.shdr.eva.mq.common.MessageOne;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * RabbitMQ 实现，支持基础发送接收、topic与fanout发布订阅、批量与缓存发送
 * RabbitMQ
 */
@Component
@Slf4j
public class RabbitMQClient implements MessageQueueClient {
    private Connection connection; // 与 RabbitMQ 的连接对象
    private Channel channel;       // 通信信道

    /**
     * 构造函数：通过明确定义参数的方式连接 RabbitMQ（适用于 localhost 环境）
     */
    public RabbitMQClient() throws IOException, TimeoutException {
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
     * 获取 Channel 实例（可用于 declare queue/exchange）
     */
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * rabbitmq fanout 模式 忽略路由key
     *
     * @param topic   主题
     * @param message 消息内容（字节数组）
     *                RabbitMQ需要开启 Confirm 模式，才有返回。
     */
    @Override
    public void sendOne(String topic, byte[] message) {
        String msgId = UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .messageId(msgId).build();
        log.info("Publishing to messageId={} exchange={} payload={}",msgId, topic, new String(message));
        try {
            getChannel().exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true); // 声明交换机
            getChannel().basicPublish(topic, "", props, message); // 发送消息
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * rabbitmq自身不支持批量，需要手动实现
     *
     * @param topic    主题
     * @param messages 消息列表（字节数组）
     */
    @Override
    public void sendBatch(String topic, List<byte[]> messages) {
        String msgId = UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .messageId(msgId).build();
        log.info("sendBatch : messageId={} exchange={}",msgId, topic);
        try {
            getChannel().exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true); // 声明交换机
            for (byte[] msg : messages) {
                getChannel().basicPublish(topic, "", props, msg); // 发送消息
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public byte[] receiveOne(String topic, String group) throws IOException {
        log.debug("Subscribing to FANOUT exchange: {}", topic);
        Assert.notNull(topic, "'topic' cannot be null");
        Assert.notNull(group, "'group' cannot be null");
        channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true);
        channel.queueBind(group, topic, "");
        GetResponse resp = channel.basicGet(group, true);
        if (resp == null)
            return null;
        return resp.getBody();
    }

    @Override
    public List<byte[]> receiveBatch(String topic, String group, int maxCount) throws IOException {
        List<byte[]> list = new ArrayList<>();
        for (int i = 0; i < maxCount; i++) {
            byte[] msg = receiveOne(topic, group);
            if (msg == null) break;
            list.add(msg);
        }
        log.info("Received {} messages from {}", list.size(), topic);
        return list;
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
    public void onMessage(String topic, String group, Consumer<MessageOne> callback) throws Exception {
        // 声明 fanout 类型交换机
        channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true);

        // 声明并绑定队列（这里 queue 就是 group）
        channel.queueDeclare(group, true, false, false, null);
        channel.queueBind(group, topic, "");

        // 定义消费者
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            byte[] body = delivery.getBody();
            String messageId = delivery.getProperties().getMessageId();
            // 构造自定义 Message 对象
            MessageOne msg = new MessageOne(topic, group, body, messageId); // messageId暂时传null或从消息属性获取
//            log.info("📨 Received message from RabbitMQ: {}", new String(body));
            callback.accept(msg);
        };

        // 开始消费
        channel.basicConsume(group, true, deliverCallback, consumerTag -> {
            log.warn("❌ Consumer cancelled: {}", consumerTag);
        });
    }

}
