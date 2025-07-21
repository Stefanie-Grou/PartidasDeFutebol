package com.example.partidasdefutebol.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class StadiumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long stadiumId;

    @NotBlank(message = "O nome do estadio é obrigatório")
    @Size(min = 3, max = 255, message = "O nome do estadio deve ser composto por, no mínimo, 3 letras.")
    @Getter
    @Setter
    private String stadiumName;

    @NotBlank(message = "O estado do estádio é mandatório para o cadastro")
    @Size(min = 2, max = 2, message = "O estado do estadio deve ser composto por 2 letras.")
    @Getter
    @Setter
    private String stadiumState;

}
