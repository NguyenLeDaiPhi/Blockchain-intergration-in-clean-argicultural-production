package com.example.logistic_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    // Tên Queue của Logistics để nhận đơn hàng
    public static final String LOGISTICS_QUEUE = "logistics_queue";
    // Tên Exchange nơi Logistics sẽ bắn thông báo trạng thái
    public static final String LOGISTICS_EXCHANGE = "logistics_exchange";
    // Routing key để gửi thông báo
    public static final String SHIPMENT_ROUTING_KEY = "shipment.status.#";

    @Bean
    public Queue logisticsQueue() {
        return new Queue(LOGISTICS_QUEUE, true);
    }

    @Bean
    public TopicExchange logisticsExchange() {
        return new TopicExchange(LOGISTICS_EXCHANGE);
    }

    // Converter để tự động chuyển Object <-> JSON
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}