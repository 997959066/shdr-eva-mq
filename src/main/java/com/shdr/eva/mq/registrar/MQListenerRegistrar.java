package com.shdr.eva.mq.registrar;


import com.shdr.eva.mq.annotation.MQListener;
import com.shdr.eva.mq.common.Message;
import com.shdr.eva.mq.rabbit.RabbitMQClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class MQListenerRegistrar implements BeanPostProcessor {

    private final RabbitMQClient rabbitMQClient;

    public MQListenerRegistrar(RabbitMQClient rabbitMQClient) {
        this.rabbitMQClient = rabbitMQClient;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 扫描每个bean的方法
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(MQListener.class)) {
                MQListener listener = method.getAnnotation(MQListener.class);
                String topic = listener.topic();
                String group = listener.group();

                // 注册监听
                try {
                    rabbitMQClient.onMessage(topic, group, msg -> {
                        try {
                            // 反射调用被注解的方法，参数类型为Message
                            if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == Message.class) {
                                method.invoke(bean, msg);
                            } else {
                                // 你可以根据需要支持更多参数或回调方式
                                throw new IllegalArgumentException("监听方法必须只接受一个参数，类型为 Message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException("MQ监听注册失败", e);
                }
                System.out.printf("✅ RabbitMQ Consumer started: topic=%s, group=%s\n", topic, group);
            }
        }
        return bean;
    }
}
