package com.example.partidasdefutebol.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @NotNull
    @Getter
    @Setter
    private Long homeClubId;

    @NotNull
    @Getter
    @Setter
    private Long awayClubId;

    @NotNull
    @Getter
    @Setter
    @PositiveOrZero
    private Long homeClubNumberOfGoals;

    @NotNull
    @Getter
    @Setter
    @PositiveOrZero
    private Long awayClubNumberOfGoals;

    @NotNull
    @Getter
    @Setter
    @Positive
    private Long stadiumId;

    @NotNull
    @Getter
    @Setter
    private LocalDateTime matchDate;
}
