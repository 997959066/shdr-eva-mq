package com.shdr.eva.mq.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MQListener {
    String topic();
    String group();
    int batchSize() default 1;
    long intervalMs() default 1000;
}
