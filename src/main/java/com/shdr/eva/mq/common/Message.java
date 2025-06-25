package com.shdr.eva.mq.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
@Getter
@Setter
public  class Message implements Serializable {

    private String topic;
    private String group;
    private byte[] msgBody;
    private String messageId;

    private Date sendTime;
    private Date receiveTime;

    public Message(String topic, String group, byte[] msgBody, String messageId) {
        this.topic = topic;
        this.group = group;
        this.msgBody = msgBody;
        this.messageId = messageId;
    }



    @Override
    public String toString() {
        return "Message{" +
                "topic='" + topic + '\'' +
                ", group='" + group + '\'' +
                ", msgBody=" + new String(msgBody) +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
