package com.shdr.eva.mq.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public  class MessagePayload implements Serializable {

    private String topic;
    private String group;
    private String msgBody;
    private List<String> msgListBody;
    private String messageId;

    private Date sendTime;
    private Date receiveTime;

    public MessagePayload(String topic, String group) {
        this.topic = topic;
        this.group = group;
    }

    public MessagePayload(String topic, String msgBody, String messageId) {
        this.topic = topic;
        this.msgBody = msgBody;
        this.messageId = messageId;
        this.sendTime = new Date();
    }

    public MessagePayload(String topic, String group, String msgBody,  String messageId) {
        this.msgBody = msgBody;
        this.group = group;
        this.topic = topic;
        this.messageId = messageId;
        this.receiveTime = new Date();
    }

    @Override
    public String toString() {
        return "MessagePayload{" +
                "topic='" + topic + '\'' +
                ", group='" + group + '\'' +
                ", msgBody='" + msgBody + '\'' +
                ", msgListBody=" + msgListBody +
                ", messageId='" + messageId + '\'' +
                ", sendTime=" + sendTime +
                ", receiveTime=" + receiveTime +
                '}';
    }


}
