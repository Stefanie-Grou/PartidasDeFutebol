package com.example.partidasdefutebol.util;

import com.example.partidasdefutebol.enums.BrazilianStates;
import com.example.partidasdefutebol.service.ClubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import java.util.stream.Stream;

public class CheckValidBrazilianState {
    private static final Logger logger = LoggerFactory.getLogger(ClubService.class);

    public static void isValidBrazilianState(String stateAcronym) {
        if (Stream.of(BrazilianStates.values()).filter
                (state -> state.name().equalsIgnoreCase(stateAcronym)).findFirst().isEmpty()) {
            throw new AmqpRejectAndDontRequeueException("O estado " + stateAcronym + " é inválido.");
        }
    }
}
