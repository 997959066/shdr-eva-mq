package com.shdr.eva.mq;

import java.util.List;

/**
 * 通用 MQ 客户端接口，定义标准操作方法
 */
public interface MessageQueueClient {
    void sendOne(String destination, byte[] message) throws Exception;

    void sendBatch(String destination, List<byte[]> messages) throws Exception;

    void storeMessage(String destination, byte[] message);

    void sendStoredMessages() throws Exception;

    byte[] receiveOne(String source) throws Exception;

    List<byte[]> receiveBatch(String source, int maxCount) throws Exception;
}
