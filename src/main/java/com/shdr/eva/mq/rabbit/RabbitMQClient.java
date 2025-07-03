package com.shdr.eva.mq.rabbit;

import com.rabbitmq.client.*;
import com.shdr.eva.mq.MessageClient;
import com.shdr.eva.mq.common.Message;
import com.shdr.eva.mq.serializer.FastJsonSerializer;
import com.shdr.eva.mq.serializer.ValueSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * RabbitMQ 实现，支持基础发送接收、topic与fanout发布订阅、批量发送
 */
@Component
@Slf4j
public class RabbitMQClient implements MessageClient {

    private Connection connection; // 与 RabbitMQ 的连接对象
    private Channel channel;       // 通信信道
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ValueSerializer<String> valueSerializer;

    // 构造函数注入
    public RabbitMQClient(ValueSerializer<String> valueSerializer) {
        this.valueSerializer = valueSerializer;
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


    /**
     * rabbitmq fanout 模式 忽略路由key
     *
     * @param message 消息内容（字节数组）
     *                RabbitMQ需要开启 Confirm 模式，才有返回。
     */
    @Override
    public void sendOne(Message message) {
        String topic = message.getTopic();
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .messageId(UUID.randomUUID().toString())
                .type(message.getBody().getClass().getName()).build();
        try {
            channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true); // 声明交换机
            //序列化消息
            String serializeBody = new RabbitMQClient(new FastJsonSerializer()).valueSerializer.serialize(message.getBody());
            log.info("sendOne Publishing to message={} ", serializeBody);
            channel.basicPublish(topic, "", props, serializeBody.getBytes()); // 发送消息
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * rabbitmq自身不支持批量，需要手动实现
     * 不同topic场景
     * 需要开启 ACK
     */
    @Override
    public void sendBatch(List<Message> messageList) {
        try {
            channel.confirmSelect(); // ✅ 开启 confirm 模式
            for (Message message : messageList) {
                String topic = message.getTopic();
                AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().
                        type(message.getBody().getClass().getName()).messageId(UUID.randomUUID().toString()).build();
                channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true); // 声明交换机
                //序列化消息
                String serializeBody = new RabbitMQClient(new FastJsonSerializer()).valueSerializer.serialize(message.getBody());
                channel.basicPublish(topic, "", props, serializeBody.getBytes()); // 发送消息
            }
            // ✅ 等待所有消息确认（同步阻塞）
            channel.waitForConfirmsOrDie(5000); // 最多等待 5 秒 ,考虑配置参数
            System.out.println("✅ 批量消息已全部发送并确认");
        } catch (IOException | InterruptedException | TimeoutException e) {
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
     */
    @Override
    public void onMessage(String topic, String group, Consumer<Message> callback) {
        try {
            // 声明 fanout 类型交换机
            channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true);
            // 声明并绑定队列（这里 queue 就是 group）
            channel.queueDeclare(group, true, false, false, null);
            channel.queueBind(group, topic, "");
            // 定义消费者
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String body = new String(delivery.getBody());
                // 接收端直接使用Class对象
                try {
                    String messageId = delivery.getProperties().getMessageId();
                    //消费者时候反序列化
                    Class<?> messageClass = Class.forName(delivery.getProperties().getType());
                    Object unSerializeBody = new RabbitMQClient(new FastJsonSerializer()).valueSerializer.unSerialize(body, messageClass);
                    // 构造自定义 Message 对象
                    Message msg = new Message(topic, group, unSerializeBody, messageId); // messageId暂时传null或从消息属性获取

                    callback.accept(msg);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            };
            // 开始消费
            channel.basicConsume(group, true, deliverCallback, consumerTag -> {
                log.warn("❌ Consumer cancelled: {}", consumerTag);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onBatchMessage(String topic, String group, int batchSize, long millisecond, Consumer<List<Message>> callback) {

        List<Message> buffer = Collections.synchronizedList(new ArrayList<>());
        try {
            channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true);
            channel.queueDeclare(group, true, false, false, null);
            channel.queueBind(group, topic, "");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String body = new String(delivery.getBody());
                String messageId = delivery.getProperties().getMessageId();
                try {
                    Class<?> messageClass = Class.forName(delivery.getProperties().getType());

                    Object unSerializeBody = new RabbitMQClient(new FastJsonSerializer()).valueSerializer.unSerialize(body, messageClass);
                    Message message = new Message<>(
                            topic,
                            group,
                            unSerializeBody,
                            messageId
                    );

                    buffer.add(message);

                    if (buffer.size() >= batchSize) {
                        flush(buffer, callback);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            };

            // 定时轮询触发批处理（防止不满 batchSize 时积压）
            scheduler.scheduleAtFixedRate(() -> {
                flush(buffer, callback);
            }, millisecond, millisecond, TimeUnit.MILLISECONDS);

            channel.basicConsume(group, true, deliverCallback, consumerTag -> {
                System.out.println("❌ 消费者取消: " + consumerTag);
            });

        } catch (IOException e) {
            throw new RuntimeException("RabbitMQ 批量监听失败", e);
        }

    }

    @Override
    public void sendDelay(Message message, int delayMs) throws IOException {
        String delayExchange = message.getTopic() + ".delay";
        String delayRoutingKey = "";

        String serializeBody = new RabbitMQClient(new FastJsonSerializer())
                .valueSerializer.serialize(message.getBody());

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .messageId(UUID.randomUUID().toString())
                .expiration(String.valueOf(delayMs)) // 动态延迟时间
                .type(message.getBody().getClass().getName())
                .build();

        channel.exchangeDeclare(delayExchange, BuiltinExchangeType.FANOUT, true);
        channel.basicPublish(delayExchange, delayRoutingKey, props, serializeBody.getBytes());
    }

    @Override
    public void onMessageDelay(String topic, String group, Consumer<Message> callback) {
        try {
            // 1. 声明死信交换机（DLX）
            channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true);

            // 2. 声明真正消费队列并绑定到死信交换机
            channel.queueDeclare(group, true, false, false, null);
            channel.queueBind(group, topic, "");

            // 3. 声明延迟队列，配置 TTL 和 DLX 指向真正交换机
            Map<String, Object> args = new HashMap<>();
//            args.put("x-message-ttl", 10000); // 这里写默认延迟时间，也可以改成动态，单位 ms
            args.put("x-dead-letter-exchange", topic); // 死信投递到真正交换机
            args.put("x-dead-letter-routing-key", ""); // fanout 类型不需要 routing key
            String delayQueueName = group + ".delay";
            channel.queueDeclare(delayQueueName, true, false, false, args);
            channel.queueBind(delayQueueName, topic + ".delay", ""); // delay交换机名称约定为 topic+".delay"

            // 4. 声明延迟交换机（用来接收延迟消息）
            channel.exchangeDeclare(topic + ".delay", BuiltinExchangeType.FANOUT, true);

            // 定义消费者监听真正消费队列
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String body = new String(delivery.getBody());
                try {
                    String messageId = delivery.getProperties().getMessageId();
                    Class<?> messageClass = Class.forName(delivery.getProperties().getType());
                    Object unSerializeBody = new RabbitMQClient(new FastJsonSerializer())
                            .valueSerializer.unSerialize(body, messageClass);
                    Message msg = new Message(topic, group, unSerializeBody, messageId);

                    callback.accept(msg);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            };

            channel.basicConsume(group, true, deliverCallback, consumerTag -> {
                log.warn("❌ Consumer cancelled: {}", consumerTag);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private <T> void flush(List<Message> buffer, Consumer<List<Message>> callback) {
        synchronized (buffer) {
            if (!buffer.isEmpty()) {
                List<Message> batch = new ArrayList<>(buffer);
                buffer.clear();
                callback.accept(batch);
            }
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
