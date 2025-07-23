package com.example.partidasdefutebol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Confrontations {
    @Getter
    @Setter
    @JsonProperty("clube_casa")
    private String homeClub;

    @Getter
    @Setter
    @JsonProperty("clube_visitante")
    private String awayClub;

    @Getter
    @Setter
    @JsonProperty("gols_do_clube_casa")
    private Integer homeClubScoredGoals;

    @Getter
    @Setter
    @JsonProperty("gols_do_clube_visitante")
    private Integer awayClubScoredGoals;

    @Getter
    @Setter
    @JsonProperty("vencedor")
    private String winner;
}
