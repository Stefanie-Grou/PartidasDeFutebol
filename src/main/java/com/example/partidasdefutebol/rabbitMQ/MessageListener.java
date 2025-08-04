package com.example.partidasdefutebol.rabbitMQ;

import com.example.partidasdefutebol.configs.RabbitConfiguration;
import com.example.partidasdefutebol.dto.QueueMessageDTO;
import com.example.partidasdefutebol.exceptions.CustomException;
import com.example.partidasdefutebol.service.ClubService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@AllArgsConstructor
public class MessageListener {
    private final ObjectMapper objectMapper;
    private final ClubService clubService;
    private static final Logger logger = LoggerFactory.getLogger(ClubService.class);
    private final Queue queue;

    @RabbitListener(queues = RabbitConfiguration.QUEUE_NAME)
    public void receiveMessage(String message) throws Exception {
        System.out.println("Received message: " + message);
        QueueMessageDTO queueMessage = objectMapper.readValue(message, QueueMessageDTO.class);
        try {
            switch (queueMessage.getOperation()) {
                case "CREATE":
                    clubService.createClub(queueMessage.getMessage());
                    break;
                case "UPDATE":
                    clubService.updateClub(queueMessage.getId(), queueMessage.getMessage());
                    break;
                default:
                    throw new CustomException("Operação" + queueMessage.getOperation() + " inválida.", 400);
            }
        } catch (CustomException exceptionMessageFromMethod) {
            logger.error("{}\n{}", exceptionMessageFromMethod + "\n Club ID: " + queueMessage.getId() + "\n" + queueMessage.getMessage(), exceptionMessageFromMethod.getMessage());
            throw new AmqpRejectAndDontRequeueException(exceptionMessageFromMethod);
        }
    }
}
