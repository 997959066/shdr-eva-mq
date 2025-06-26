package com.shdr.eva.mq;

import com.shdr.eva.mq.common.Message;
import com.shdr.eva.mq.common.MessagePayload;

import java.util.List;
import java.util.function.Consumer;


public interface MessageClient {

    /**
     * 发送单条消息到指定topic
     * @throws Exception 发送失败时抛出异常
     */
    void sendOne(MessagePayload message) ;

    /**
     * 批量发送多条消息到指定topic
     * 当前批次的messageId相同
     */
    void sendBatch(MessagePayload message) ;

    /**
     * 消息持续监听
     * @param callback
     * @throws Exception
     */
    void onMessage(MessagePayload message, Consumer<MessagePayload> callback) throws Exception;


}
