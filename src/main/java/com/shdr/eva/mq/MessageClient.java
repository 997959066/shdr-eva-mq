package com.shdr.eva.mq;

import com.shdr.eva.mq.common.Message;
import com.shdr.eva.mq.common.MessagePayload;

import java.util.List;
import java.util.function.Consumer;


public interface MessageClient {

    /**
     * 发送单条消息
     * @param messagePayload
     */
    void sendOne(MessagePayload messagePayload) ;

    /**
     * 批量发送条消息
     * @param messagePayload
     */
    void sendBatch(MessagePayload messagePayload) ;

    /**
     * 消息监听
     * @param messagePayload
     * @param callback
     * @throws Exception
     */
    void onMessage(MessagePayload messagePayload, Consumer<MessagePayload> callback);


}
