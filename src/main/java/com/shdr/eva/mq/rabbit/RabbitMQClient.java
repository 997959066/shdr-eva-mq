package com.shdr.eva.mq.rabbit;

import com.rabbitmq.client.*;
import com.shdr.eva.mq.MessageQueueClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ 实现，支持基础发送接收、topic与fanout发布订阅、批量与缓存发送
 */
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

    @Override
    public void sendOne(String topic, byte[] message) throws IOException {
        log.info("Publishing to exchange={}payload={}", topic, new String(message));
        getChannel().exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true); // 声明交换机
        getChannel().basicPublish(topic, "", null, message); // 发送消息
    }

    @Override
    public void sendBatch(String topic, List<byte[]> messages) throws IOException {
        for (byte[] msg : messages) {
            sendOne(topic, msg); // 逐条发送
        }
    }


    @Override
    public byte[] receiveOne(String topic,String group ) throws IOException {
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
    public List<byte[]> receiveBatch(String topic,String group, int maxCount) throws IOException {
        List<byte[]> list = new ArrayList<>();
        for (int i = 0; i < maxCount; i++) {
            byte[] msg = receiveOne(topic,group);
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
}
