package com.example.partidasdefutebol.util;

import com.example.partidasdefutebol.enums.BrazilianStates;

public class isValidBrazilianState {
    private String stateAcronymEnum;

    public static boolean isValidBrazilianState(String stateAcronym) {
        for (BrazilianStates estado : BrazilianStates.values()) {
            if (estado.name().equalsIgnoreCase(stateAcronym)) {
                return true;
            }
        }
        return false;
    }
}