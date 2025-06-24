package com.shdr.eva.mq.rocketmq;


import com.shdr.eva.mq.MessageQueueClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RocketMQClient implements MessageQueueClient {

    private final DefaultMQProducer producer;
    private final String namesrvAddr = "localhost:9876";

    public RocketMQClient() throws Exception {
        producer = new DefaultMQProducer("rocketmq-producer-group");
        producer.setNamesrvAddr(namesrvAddr);
        producer.start();
        log.info("RocketMQ producer started.");
    }

    @Override
    public void sendOne(String exchange, byte[] message) throws Exception {
        //组只是逻辑分组名，不影响广播行为➤ 可以固定一个名字，也可以多个生产者共享，不影响广播
        DefaultMQProducer producer = new DefaultMQProducer("rocketmq-producer-group");
        producer.setNamesrvAddr(namesrvAddr);
        producer.start();

        Message msg = new Message(exchange, "TagA", message);
        producer.send(msg);
        System.out.println(msg.toString());
        producer.shutdown();
    }

    @Override
    public void sendBatch(String exchange, List<byte[]> messages) throws Exception {
        for (byte[] msg : messages) {
            sendOne(exchange, msg); // 逐条发送
        }
    }

    /**
     * 广播模式下消费者必须「实时在线」！
     * 特点：
     * 消息不会存储给离线消费者	如果你程序不在线，消息就直接“飞走”了，不会重投、也不会补发
     * 不能重复消费历史消息	重启程序，只能接收重启之后的广播消息
     * 必须一直监听（常驻消费者）	类似“广播电台”：你收音机没打开，就错过节目了
     *
     *
     * 集群模式下 RocketMQ 会保留消息直到被消费
     * @param exchange 交换机名称
     * @param queue
     * @return
     * @throws Exception
     */
    @Override
    public byte[] receiveOne(String exchange, String queue) throws Exception {

        final byte[][] result = {null};


        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(queue);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.subscribe(exchange, "*");

        CountDownLatch latch = new CountDownLatch(1);

        // 2. 注册监听器，仅处理一条消息
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            if (!msgs.isEmpty() && result[0] == null) {
                MessageExt msg = msgs.get(0);
                result[0] = msg.getBody();
                System.out.println("📩 Received: " + new String(msg.getBody(), StandardCharsets.UTF_8));
                latch.countDown();
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        // 3. 启动消费者
        consumer.start();

        // 保持主线程不退出
        new CountDownLatch(1).await();
        consumer.shutdown();
        return result[0];
    }

    @Override
    public List<byte[]> receiveBatch(String exchange, String queue, int maxCount) throws Exception {
        List<byte[]> received = Collections.synchronizedList(new ArrayList<>());

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(queue);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.subscribe(exchange, "*");
        consumer.setMessageModel(MessageModel.BROADCASTING); // ✅ 广播模式

        CountDownLatch latch = new CountDownLatch(maxCount);

        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                if (received.size() < maxCount) {
                    System.out.println("📩 Received Batch Message:");
                    System.out.println("  📨 Body : " + new String(msg.getBody(), StandardCharsets.UTF_8));
                    System.out.println("  📌 Topic: " + msg.getTopic());
                    System.out.println("  🏷️  Tag  : " + msg.getTags());
                    System.out.println("  🆔 MsgId: " + msg.getMsgId());

                    received.add(msg.getBody());
                    latch.countDown();
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();

        // 最多等待 maxWaitMillis 毫秒
        latch.await(1000000000, TimeUnit.MILLISECONDS);

        consumer.shutdown();
        return received;
    }


}
