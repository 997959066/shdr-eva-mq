package com.shdr.eva.mq.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.shdr.eva.mq.serializer.ValueSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class Message<T> {

    private String topic;

    private String group;

    private String messageId;

    private T body;

    private Date sendTime;

    private Date receiveTime;


    // 用于生产者，序列化
    public Message(String topic, T body) {
        this.topic = topic;
        this.body = body;
        this.sendTime = new Date();
    }

    // 用于消费者，反序列化
    public Message(String topic, String group, T body, String messageId) {
        this.topic = topic;
        this.group = group;
        this.body = body;
        this.messageId = messageId;
        this.receiveTime = new Date();
    }


    @Override
    public String toString() {
        return "Message{" +
                "topic='" + topic + '\'' +
                ", group='" + group + '\'' +
                ", messageId='" + messageId + '\'' +
                ", body=" + body +
                ", sendTime=" + sendTime +
                ", receiveTime=" + receiveTime +
                '}';
    }
}
