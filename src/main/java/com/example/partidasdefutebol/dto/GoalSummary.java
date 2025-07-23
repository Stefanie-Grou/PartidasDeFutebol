package com.example.partidasdefutebol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class GoalSummary {
    @Getter
    @Setter
    @JsonProperty("gols_feitos")
    public Integer totalOfPositiveGoals;

    @Getter
    @Setter
    @JsonProperty("gols_sofridos")
    public Integer totalOfNegativeGoals;

    @Getter
    @Setter
    @JsonProperty("total_de_vitorias")
    public Integer totalOfVictories;

    @Getter
    @Setter
    @JsonProperty("total_de_empates")
    public Integer totalOfDraws;

    @Getter
    @Setter
    @JsonProperty("total_de_derrotas")
    public Integer totalOfDefeats;
}
