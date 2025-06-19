package com.shdr.eva.mq.rabbit;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
public class RabbitConnectTest {
    public static void main(String[] args) throws Exception {
        // 尝试更基础的连接方式
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);

        factory.setAutomaticRecoveryEnabled(true);

        try (Connection connection = factory.newConnection()) {
            System.out.println("✅ 成功连接到 RabbitMQ！");
        } catch (Exception e) {
            e.printStackTrace();  // 👈 把这里打印的贴出来，我能定位具体问题
        }
    }
}
