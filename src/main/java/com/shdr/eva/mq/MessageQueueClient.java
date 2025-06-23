package com.shdr.eva.mq;

import java.util.List;

/**
 * 广播模式
 */
public interface MessageQueueClient {

    /**
     * 发送单条消息到指定目的地（队列）
     * @param exchange 交换机名称
     * @param message 消息内容（字节数组）
     * @throws Exception 发送失败时抛出异常
     */
    void sendOne(String exchange,String queue,  byte[] message) throws Exception;
    /**
     * 批量发送多条消息到指定目的地（队列）
     * @param exchange 交换机名称
     * @param messages 消息列表（字节数组）
     * @throws Exception 发送失败时抛出异常
     */
    void sendBatch(String exchange,String queue, List<byte[]> messages) throws Exception;
    /**
     * 从指定队列拉取一条消息（自动确认）
     * @param exchange 交换机名称
     * @return 获取到的消息字节数组；如无消息则返回 null
     * @throws Exception 拉取失败时抛出异常
     */
    byte[] receiveOne(String exchange,String queue ) throws Exception;

    /**
     * 批量从指定队列拉取消息（自动确认）
     * @param exchange 交换机名称
     * @param maxCount 最多拉取消息数
     * @return 消息字节数组列表；可能为空列表
     * @throws Exception 拉取失败时抛出异常
     */
    List<byte[]> receiveBatch(String exchange,String queue, int maxCount) throws Exception;

    /**
     * 暂存单条消息，不立即发送
     * @param exchange 目标队列名称
     * @param message 待缓存的消息内容
     */
    void storeMessage(String exchange, byte[] message);
    /**
     * 发送所有已缓存的消息
     * @throws Exception 如果发送失败抛出异常
     */
    void sendStoredMessages() throws Exception;

}
