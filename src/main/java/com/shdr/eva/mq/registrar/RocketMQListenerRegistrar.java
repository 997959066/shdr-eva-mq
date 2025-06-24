package com.shdr.eva.mq.registrar;


import com.shdr.eva.mq.annotation.RocketMQListener;
import com.shdr.eva.mq.common.Message;
import jakarta.annotation.PostConstruct;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class RocketMQListenerRegistrar {

    private final ApplicationContext context;

    private String namesrvAddr= "localhost:9876";;

    public RocketMQListenerRegistrar(ApplicationContext context) {
        this.context = context;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerListeners() throws Exception {
        // 扫描所有bean
        Map<String, Object> beans = context.getBeansWithAnnotation(Component.class);

        for (Object bean : beans.values()) {
            Class<?> clazz = bean.getClass();
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                RocketMQListener listener = method.getAnnotation(RocketMQListener.class);
                if (listener != null) {
                    registerRocketMQConsumer(bean, method, listener.topic(), listener.group());
                }
            }
        }
    }

    private void registerRocketMQConsumer(Object bean, Method method, String topic, String group) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.subscribe(topic, group);

        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msgExt : msgs) {
                // 构造通用Message对象
                Message message = new Message(
                        msgExt.getTopic(),
                        group,
                        msgExt.getBody(),
                        msgExt.getMsgId()
                );

                try {
                    // 调用业务方法，传入Message对象
                    method.invoke(bean, message);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 这里可以根据需求做失败重试或者忽略
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();

        System.out.printf("✅ RocketMQ Consumer started: topic=%s, group=%s\n", topic, group);
    }
}

