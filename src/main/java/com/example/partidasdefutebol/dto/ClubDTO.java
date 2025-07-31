package com.example.partidasdefutebol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
public class ClubDTO {
    @JsonProperty("nome")
    @Getter
    @Setter
    String NameDTO;

    @JsonProperty("estado")
    @Getter
    @Setter
    String StateDTO;

    @Getter
    @Setter
    @JsonProperty("em-atividade")
    Boolean isActiveDTO;

    @Getter
    @Setter
    @JsonProperty("criado_em")
    LocalDate createdOnDTO;
}
