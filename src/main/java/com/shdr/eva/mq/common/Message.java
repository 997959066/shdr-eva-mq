package com.shdr.eva.mq.common;

import java.io.Serializable;

public  class Message implements Serializable {

    private String topic;
    private String group;
    private byte[] msgBody;
    private String traceId;

    public Message(String topic, String group, byte[] msgBody, String traceId) {
        this.topic = topic;
        this.group = group;
        this.msgBody = msgBody;
        this.traceId = traceId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public byte[] getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(byte[] msgBody) {
        this.msgBody = msgBody;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
