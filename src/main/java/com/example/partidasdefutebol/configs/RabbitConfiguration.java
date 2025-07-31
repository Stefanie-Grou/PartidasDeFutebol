package com.example.partidasdefutebol.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    public static final String QUEUE_NAME = "club-queue";

    @Bean
    public Queue matchQueue() {
        return new Queue(QUEUE_NAME);
    }
}