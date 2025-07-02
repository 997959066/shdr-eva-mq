package com.shdr.eva.mq.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MQListener {
    String topic();
    String group();

    // 新增参数
    int batchSize() default 1;          // 默认为单条
    long intervalMs() default 5000;     // 默认5秒定时触发
}
