package com.example.partidasdefutebol.util;

import com.example.partidasdefutebol.enums.BrazilianStates;
import com.example.partidasdefutebol.service.ClubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import java.util.Arrays;

public class CheckValidBrazilianState {
    private static final Logger logger = LoggerFactory.getLogger(ClubService.class);

    public static boolean isValidBrazilianState(String stateAcronym) {
        for (BrazilianStates estado : BrazilianStates.values()) {
            if (estado.name().equalsIgnoreCase(stateAcronym)) {
                return true;
            }
        }
        return false;
    }
}
