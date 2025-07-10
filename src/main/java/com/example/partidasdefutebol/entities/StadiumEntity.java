package com.example.partidasdefutebol.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.example.partidasdefutebol.Enums.BrazilianStates.isValidBrazilianState;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class StadiumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long stadiumId;

    @NotBlank
    @Size(min = 3, max = 255)
    @Getter
    @Setter
    private String stadiumName;

    @NotBlank
    @Size(min = 2, max = 2)
    @Getter
    @Setter
    private String stadiumState;

    public void setStadiumState(String stadiumState) {
        if (isValidBrazilianState(stadiumState)) {
            this.stadiumState = stadiumState;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
