package com.shdr.eva.mq;

import java.util.List;
import java.util.function.Consumer;


public interface MessageQueueClient {

    /**
     * 发送单条消息到指定topic
     * @param topic 主题
     * @param message 消息内容（字节数组）
     * @throws Exception 发送失败时抛出异常
     */
    void sendOne(String topic, byte[] message) throws Exception;

    /**
     * 批量发送多条消息到指定topic
     * @param topic 主题
     * @param messages 消息列表（字节数组）
     * @throws Exception 发送失败时抛出异常
     */
    void sendBatch(String topic, List<byte[]> messages) throws Exception;


    /**
     * 持续监听（异步消费）
     * @param topic
     * @param group
     * @param callback
     * @throws Exception
     */
    void onMessage(String topic, String group,  Consumer<byte[]> callback) throws Exception;
    /**
     * 从指定队列拉取一条消息
     * @param topic 主题
     * @return 获取到的消息字节数组；如无消息则返回 null
     * @throws Exception 拉取失败时抛出异常
     */
    byte[] receiveOne(String topic,String group ) throws Exception;

    /**
     * 批量从指定队列拉取消息
     * @param topic 主题
     * @param maxCount 最多拉取消息数
     * @return 消息字节数组列表；可能为空列表
     * @throws Exception 拉取失败时抛出异常
     */
    List<byte[]> receiveBatch(String topic,String group, int maxCount) throws Exception;


}
