package com.example.partidasdefutebol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class SummaryByOpponent {
    @Getter
    @Setter
    @JsonProperty("nome_do_oponente")
    private String opponent;
    
    @Getter
    @Setter
    @JsonProperty("total_de_jogos")
    private Integer totalOfMatchesPlayed;
    
    @Getter
    @Setter
    @JsonProperty("total_de_vitorias")
    private Integer totalOfMatchesWon;
    
    @Getter
    @Setter
    @JsonProperty("total_de_empates")
    private Integer totalOfMatchesDrawn;

    @Getter
    @Setter
    @JsonProperty("total_de_derrotas")
    private Integer totalOfMatchesLost;

    @Getter
    @Setter
    @JsonProperty("total_de_gols_feitos")
    private Integer totalOfGoalsScored;

    @Getter
    @Setter
    @JsonProperty("total_de_gols_sofridos")
    private Integer totalOfGoalsConceded;
}
