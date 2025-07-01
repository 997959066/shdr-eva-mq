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

    private String body;

    private Date sendTime;

    private Date receiveTime;

    protected ValueSerializer<String> valueSerializer;

    public Message(String topic, String group) {
        this.topic = topic;
        this.group = group;
    }

    /**
     * 构造完成序列化
     */
    public Message(String topic, T body, String messageId) {
        this.topic = topic;
        this.body = JSON.toJSONString(body, JSONWriter.Feature.WriteClassName);
        this.messageId = messageId;
        this.sendTime = new Date();
    }

    public Message(String topic, String group, T body, String messageId) {
        this.topic = topic;
        this.group = group;
        this.body = (String) JSON.parse(String.valueOf(body), JSONReader.Feature.SupportAutoType);
        this.messageId = messageId;
        this.receiveTime = new Date();
    }

    //反序列化
    public <T> T getBody() {
        Object o = JSON.parse(body, JSONReader.Feature.SupportAutoType);
//        Object o = valueSerializer.unSerialize(body);
        return (T) o;
    }


    @Override
    public String toString() {
        return "MessagePayload{" +
                "messageId='" + messageId + '\'' +
                ", body='" + body + '\'' +
                ", sendTime=" + sendTime +
                ", receiveTime=" + receiveTime +
                ", valueSerializer=" + valueSerializer +
                '}';
    }
}
