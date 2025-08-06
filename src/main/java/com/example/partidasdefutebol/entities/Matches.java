package com.example.partidasdefutebol.entities;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Estidade Partida. \n" +
                    "Registrado no banco de dados a partir do input na Controller.")
public class Matches {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Schema(description = "Identificador (Long) da partida", example = "1 / 2 / 3")
    private Long id;

    @NotNull
    @Getter
    @Setter
    @Schema(description = "Identificador (Long) do clube de casa", example = "1 / 2 / 3")
    private Long homeClubId;

    @NotNull
    @Getter
    @Setter
    @Schema(description = "Identificador (Long) do clube visitante", example = "1 / 2 / 3")
    private Long awayClubId;

    @NotNull
    @Getter
    @Setter
    @PositiveOrZero
    @Schema(description = "Quantidade de gols do clube de casa. Sempre maior ou igual a zero.",
            example = "1 / 2 / 3 / ...")
    private Long homeClubNumberOfGoals;

    @NotNull
    @Getter
    @Setter
    @PositiveOrZero
    @Schema(description = "Quantidade de gols do clube visitante. Sempre maior ou igual a zero.",
            example = "1 / 2 / 3 / ...")
    private Long awayClubNumberOfGoals;

    @NotNull
    @Getter
    @Setter
    @Positive
    @Schema(description = "Identificador (Long) do estádio", example = "1 / 2 / 3")
    private Long stadiumId;

    @NotNull
    @Getter
    @Setter
    @Schema(description = "Data e hora da partida, no modelo YYYY-MM-DDTHH:MM:SS."
            , example = "2022-10-10T15:30:00")
    private LocalDateTime matchDate;
}
