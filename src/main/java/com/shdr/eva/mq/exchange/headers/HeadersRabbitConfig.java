package com.shdr.eva.mq.exchange.headers;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HeadersRabbitConfig {

    public static final String HEADERS_EXCHANGE = "headers.exchange";
    public static final String HEADERS_QUEUE = "headers.queue";

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Queue headersQueue() {
        return new Queue(HEADERS_QUEUE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(headersQueue()).to(headersExchange())
                .whereAll("type", "format").exist();
    }
}
