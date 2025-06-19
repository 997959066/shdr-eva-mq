package com.shdr.eva.mq;

import java.util.List;

/**
 * 通用 MQ 客户端接口，定义标准操作方法
 */
public interface MessageQueueClient {


    void sendOne(String exchange, byte[] message) throws Exception;

    void sendBatch(String exchange, List<byte[]> messages) throws Exception;


    byte[] receiveOne(String exchange) throws Exception;

    List<byte[]> receiveBatch(String source, int maxCount) throws Exception;


    void storeMessage(String destination, byte[] message);

    void sendStoredMessages() throws Exception;

}
