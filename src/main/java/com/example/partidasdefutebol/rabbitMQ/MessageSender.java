package com.example.partidasdefutebol.rabbitMQ;

import com.example.partidasdefutebol.configs.RabbitConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;

    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessageToQueue(String message) {
        rabbitTemplate.convertAndSend(RabbitConfiguration.QUEUE_NAME, message);
        System.out.println("Message sent: " + message);
    }

}
