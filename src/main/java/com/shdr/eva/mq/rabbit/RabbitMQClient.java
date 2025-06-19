package com.shdr.eva.mq.rabbit;
import com.rabbitmq.client.*;
import com.shdr.eva.mq.MessageQueueClient;
import lombok.extern.slf4j.Slf4j;
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
    private List<PendingMessage> buffer = new ArrayList<>(); // 消息缓存列表

    // 内部类：用于缓存发送失败的消息
    private static class PendingMessage {
        String destination;
        byte[] message;
        PendingMessage(String destination, byte[] message) {
            this.destination = destination;
            this.message = message;
        }
    }

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

    /** 获取 Channel 实例（可用于 declare queue/exchange） */
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void sendOne(String destination, byte[] message) throws IOException {
        log.info("Sending message to {}: {}", destination, new String(message));
        channel.queueDeclare(destination, true, false, false, null); // 确保目标队列存在
        channel.basicPublish("", destination, null, message);       // 发送消息到默认 direct 交换机
        try {
            if (!channel.waitForConfirms()) { // 等待确认
                log.error("Message to {} not acknowledged by broker!", destination);
                throw new IOException("Publish not confirmed by broker.");
            }
            log.info("Message to {} confirmed by broker.", destination);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Publish confirmation interrupted.", e);
        }
    }

    @Override
    public void sendBatch(String destination, List<byte[]> messages) throws IOException {
        for (byte[] msg : messages) {
            sendOne(destination, msg); // 逐条发送
        }
    }

    @Override
    public void storeMessage(String destination, byte[] message) {
        log.info("Storing message for {}: {}", destination, new String(message));
        buffer.add(new PendingMessage(destination, message)); // 加入缓存队列
    }

    @Override
    public void sendStoredMessages() throws IOException {
        for (PendingMessage pm : buffer) {
            sendOne(pm.destination, pm.message); // 批量重发
        }
        log.info("Sent {} stored messages.", buffer.size());
        buffer.clear(); // 清空缓存
    }

    @Override
    public byte[] receiveOne(String source) throws IOException {
        log.info("Receiving message from {}", source);
        GetResponse response = channel.basicGet(source, true); // 拉取一条消息并自动确认
        if (response == null) {
            log.info("No message received from {}", source);
            return null;
        }
        log.info("Received message from {}: {}", source, new String(response.getBody()));
        return response.getBody();
    }

    @Override
    public List<byte[]> receiveBatch(String source, int maxCount) throws IOException {
        List<byte[]> list = new ArrayList<>();
        for (int i = 0; i < maxCount; i++) {
            byte[] msg = receiveOne(source);
            if (msg == null) break;
            list.add(msg);
        }
        log.info("Received {} messages from {}", list.size(), source);
        return list;
    }

    /** 发布到 Topic 交换机 */
    public void publishTopic(String exchange, String routingKey, byte[] message) throws IOException {
        publishExchange(exchange, routingKey, message, BuiltinExchangeType.TOPIC);
    }

    /** 订阅 Topic 消息 */
    public List<byte[]> subscribeTopic(String exchange, String bindingKey) throws IOException {
        return subscribeExchange(exchange, bindingKey, BuiltinExchangeType.TOPIC);
    }

    /** 发布到 Fanout（广播）交换机 */
    public void publishFanout(String exchange, byte[] message) throws IOException {
        publishExchange(exchange, "", message, BuiltinExchangeType.FANOUT);
    }

    /** 订阅 Fanout 消息 */
    public List<byte[]> subscribeFanout(String exchange) throws IOException {
        return subscribeExchange(exchange, "", BuiltinExchangeType.FANOUT);
    }

    /** 通用交换机发布逻辑（支持 direct、fanout、topic） */
    public void publishExchange(String exchange, String routingKey, byte[] message, BuiltinExchangeType type) throws IOException {
        log.info("Publishing to exchange={} type={} routingKey={} payload={}",
                exchange, type, routingKey, new String(message));
        channel.exchangeDeclare(exchange, type, true); // 声明交换机
        channel.basicPublish(exchange, routingKey, null, message); // 发送消息
    }

    /** 通用交换机订阅逻辑 */
    public List<byte[]> subscribeExchange(String exchange, String bindingKey, BuiltinExchangeType type) throws IOException {
        channel.exchangeDeclare(exchange, type, true); // 声明交换机
        String queueName = channel.queueDeclare().getQueue(); // 创建临时队列
        channel.queueBind(queueName, exchange, bindingKey);   // 绑定队列到交换机
        log.info("Queue {} bound to exchange {} with key {}", queueName, exchange, bindingKey);

        List<byte[]> msgs = new ArrayList<>();
        while (true) {
            GetResponse response = channel.basicGet(queueName, true); // 拉取消息
            if (response == null) break;
            msgs.add(response.getBody());
        }
        log.info("Received {} messages from exchange {} via queue {}", msgs.size(), exchange, queueName);
        return msgs;
    }

    /** 关闭资源 */
    public void close() throws IOException, TimeoutException {
        if (channel != null && channel.isOpen()) {
            try {
                log.info("Closing RabbitMQ channel.");
                channel.close();
            } catch (Exception ignored) {}
        }
        if (connection != null && connection.isOpen()) {
            try {
                log.info("Closing RabbitMQ connection.");
                connection.close();
            } catch (Exception ignored) {}
        }
    }
}
