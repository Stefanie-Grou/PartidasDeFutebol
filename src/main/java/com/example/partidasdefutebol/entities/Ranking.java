package com.example.partidasdefutebol.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class Ranking {
    @Getter
    @Setter
    @JsonProperty("clube")
    private String clubName;

    @Getter
    @Setter
    @JsonProperty("total")
    private Integer rankingFactor;
}
