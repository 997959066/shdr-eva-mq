package com.shdr.eva.mq.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RabbitMQListener {
    String topic();
    String group();
}
