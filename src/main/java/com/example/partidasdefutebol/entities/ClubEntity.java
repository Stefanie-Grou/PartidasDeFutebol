package com.example.partidasdefutebol.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ClubEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Setter
    @Getter
    @NotBlank(message = "O nome do clube é mandatório para o cadastro")
    @Size(min = 2, max = 50, message = "O nome do clube deve ser de, no mínimo, duas letras.")
    private String clubName;

    @Getter
    @NotBlank(message = "O estado do clube é mandatório para o cadastro")
    @Size(min = 2, max = 2, message = "O estado do clube deve ser composto por duas letras.")
    private String stateAcronym;

    @Setter
    @Getter
    @PastOrPresent(message = "A data de criação do clube deve ser presente ou passada.")
    private LocalDate createdOn;

    @Setter
    @Getter
    private Boolean isActive;

    public void setStateAcronym(String stateAcronym) {
        this.stateAcronym = stateAcronym.toUpperCase();
    }
}
