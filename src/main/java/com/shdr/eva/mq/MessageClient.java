package com.shdr.eva.mq;

import com.shdr.eva.mq.common.Message;
import java.util.List;
import java.util.function.Consumer;


public interface MessageClient {

    /**
     * 发送单条消息
     * @param message
     */
    void sendOne(Message message) ;

    /**
     * 批量发送条消息
     * @param messageList
     */
    void sendBatch(List<Message> messageList) ;

    /**
     * 消息监听
     * @param topic
     * @param group
     * @param callback
     */
    void onMessage(String topic,String group,Consumer<Message> callback);



}
