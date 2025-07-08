package com.example.partidasdefutebol.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import static com.example.partidasdefutebol.dto.BrazilianStates.isValidBrazilianState;

@Entity
@Table(name = "clubs"
        , uniqueConstraints = {
        @UniqueConstraint(columnNames = {"club_name", "stateAcronym"})}
        )
@NoArgsConstructor
@AllArgsConstructor
public class ClubEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @Getter
    @NotBlank
    private String clubName;

    @Getter
    @NotBlank
    @Size(min = 2, max = 2)
    private String stateAcronym;

    @Setter
    @Getter
    @PastOrPresent
    private LocalDate createdOn;

    @Setter
    @Getter
    private Boolean isActive;

    public void setStateAcronym(String stateAcronym) {
        if (isValidBrazilianState(stateAcronym)) {
            this.stateAcronym = stateAcronym;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
