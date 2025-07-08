package com.example.partidasdefutebol.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.example.partidasdefutebol.dto.BrazilianStates.isValidBrazilianState;

@Entity
@Table(name = "stadiums", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"stadiumName", "stadiumState"})})
@AllArgsConstructor
@NoArgsConstructor
public class StadiumEntity {
    @Id
    @GeneratedValue
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
