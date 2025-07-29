package com.example.partidasdefutebol.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Routs {
    @Getter
    @Setter
    @JsonProperty("partida")
    private String clubsOnMatch;

    @Getter
    @Setter
    @JsonProperty("estadio")
    private String stadiumName;

    @Getter
    @Setter
    @JsonProperty("ocorreu_em")
    private String matchDate;
}
