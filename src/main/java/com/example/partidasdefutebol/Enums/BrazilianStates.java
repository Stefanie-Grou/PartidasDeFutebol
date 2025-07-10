package com.example.partidasdefutebol.Enums;

public enum BrazilianStates {
    AC,
    AL,
    AP,
    AM,
    BA,
    CE,
    DF,
    ES,
    GO,
    MA,
    MT,
    MS,
    MG,
    PA,
    PB,
    PR,
    PE,
    PI,
    RJ,
    RN,
    RS,
    RO,
    RR,
    SC,
    SP,
    SE,
    TO;

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

