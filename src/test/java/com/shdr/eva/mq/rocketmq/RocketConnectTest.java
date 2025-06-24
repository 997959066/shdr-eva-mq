package com.shdr.eva.mq.rocketmq;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RocketConnectTest {

    private static final String NAMESRV_ADDR = "localhost:9876";
    private static final String TOPIC = "testTopic";
    private static final String TAG = "tagA";

    @Test
    public void testRocketMQConnectionAndSend() throws Exception {
        // 初始化生产者，并指定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("test-connection-group");

        // 设置 nameserver 地址
        producer.setNamesrvAddr(NAMESRV_ADDR);

        // 设置发送超时时间为10秒（默认3秒容易失败）
        producer.setSendMsgTimeout(10000);

        // 启动生产者
        producer.start();

        // 构造消息对象（topic, 标签, 消息体）
        Message message = new Message(TOPIC, TAG, "Hello RocketMQ".getBytes());

        // 发送消息，并获得发送结果
        SendResult result = producer.send(message);

        // 打印发送结果
        System.out.println("🚀 RocketMQ SendResult: " + result);

        // 验证发送状态为成功
        assertNotNull(result);
        assertEquals("SEND_OK", result.getSendStatus().name());

        // 关闭生产者
        producer.shutdown();
    }


    @Test
    public void testRocketMQConsume() throws Exception {
        // 初始化消费者，指定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-consumer-group");

        // 设置 nameserver 地址
        consumer.setNamesrvAddr(NAMESRV_ADDR);

        // 订阅topic和tag
        consumer.subscribe(TOPIC, TAG);
        consumer.setMessageModel(MessageModel.BROADCASTING);//广播模式

        // 注册消息监听器，打印接收到的消息
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                System.out.println("📩 Received message: " + new String(msg.getBody()) +
                        ", topic: " + msg.getTopic() +
                        ", tags: " + msg.getTags());
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        // 启动消费者
        consumer.start();
        System.out.println("✅ Consumer started, waiting for messages...");

        // 为了测试，等5秒钟看是否收到消息
        Thread.sleep(500000);

        // 关闭消费者
        consumer.shutdown();
    }
}
