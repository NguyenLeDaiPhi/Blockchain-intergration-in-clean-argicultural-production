package com.bicap.blockchain_adapter_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String BLOCKCHAIN_QUEUE = "blockchain.order.queue";
    public static final String ORDER_COMPLETED_KEY = "order.completed";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue blockchainQueue() {
        return new Queue(BLOCKCHAIN_QUEUE);
    }

    @Bean
    public Binding binding(Queue blockchainQueue, TopicExchange orderExchange) {
        return BindingBuilder
                .bind(blockchainQueue)
                .to(orderExchange)
                .with(ORDER_COMPLETED_KEY);
    }

    // 🔥 JSON CONVERTER TOÀN CỤC
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
