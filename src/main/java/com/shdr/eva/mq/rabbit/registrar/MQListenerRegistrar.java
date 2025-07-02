package com.shdr.eva.mq.rabbit.registrar;


import com.shdr.eva.mq.annotation.MQListener;
import com.shdr.eva.mq.common.Message;
import com.shdr.eva.mq.rabbit.RabbitMQClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Component
public class MQListenerRegistrar implements BeanPostProcessor {

    private final RabbitMQClient rabbitMQClient;

    public MQListenerRegistrar(RabbitMQClient rabbitMQClient) {
        this.rabbitMQClient = rabbitMQClient;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(MQListener.class)) {
                MQListener listener = method.getAnnotation(MQListener.class);
                String topic = listener.topic();
                String group = listener.group();
                int batchSize = listener.batchSize();
                long intervalMs = listener.intervalMs();

                try {
                    Class<?>[] paramTypes = method.getParameterTypes();

                    if (paramTypes.length == 1 && paramTypes[0] == Message.class) {
                        // ✅ 单条监听
                        rabbitMQClient.onMessage(topic, group, msg -> {
                            try {
                                method.invoke(bean, msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (paramTypes.length == 1 &&
                            List.class.isAssignableFrom(paramTypes[0]) &&
                            method.getGenericParameterTypes()[0].getTypeName().startsWith("java.util.List<com.shdr.eva.mq.common.Message")) {

                        // ✅ 批量监听
                        rabbitMQClient.onBatchMessage(topic, group, batchSize, intervalMs, messages -> {
                            try {
                                method.invoke(bean, messages);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        throw new IllegalArgumentException("监听方法参数必须为 Message 或 List<Message>");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("MQ监听注册失败", e);
                }

                System.out.printf("✅ RabbitMQ Consumer started: topic=%s, group=%s\n", topic, group);
            }
        }
        return bean;
    }

}
