package com.bicap.blockchain_adapter_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Lấy tên từ file config vừa sửa
    @Value("${bicap.rabbitmq.queue.request}")
    private String requestQueueName;

    @Value("${bicap.rabbitmq.queue.response}")
    private String responseQueueName;

    @Value("${bicap.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${bicap.rabbitmq.routing-key.response}")
    private String responseRoutingKey;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    // Queue nhận tin nhắn từ Farm
    @Bean
    public Queue requestQueue() {
        return new Queue(requestQueueName, true);
    }

    // Queue trả kết quả về Farm
    @Bean
    public Queue responseQueue() {
        return new Queue(responseQueueName, true);
    }

    // Binding cho queue trả về (Queue nhận không cần binding ở đây vì Farm đã bind rồi)
    @Bean
    public Binding responseBinding() {
        return BindingBuilder.bind(responseQueue()).to(exchange()).with(responseRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}