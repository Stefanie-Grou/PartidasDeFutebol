package com.example.partidasdefutebol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public class RankingDTO {
    @Getter
    @Setter
    @JsonProperty("clube")
    private String clubName;

    @Getter
    @Setter
    @JsonProperty("total")
    private Integer rankingFactor;

    @Override
    public String toString() {
        return "RankingDTO{" +
                "clubName='" + clubName +
                ", pontos =" + rankingFactor +
                "}\n";
    }
}
