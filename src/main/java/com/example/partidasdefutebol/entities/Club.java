package com.example.partidasdefutebol.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

import static com.example.partidasdefutebol.util.CheckValidBrazilianState.isValidBrazilianState;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = """
        Entidade Clube.\s
        Registrado no banco de dados a partir do input na Controller. \n
        Por regra, não deve haver duplicatas entre nome e estado de cada registro""")
public class Club {
    @Schema(description = "ID do item", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Schema(description = "Nome do clube", example = "Operário")
    @Setter
    @Getter
    @NotBlank(message = "O nome do clube é mandatório para o cadastro")
    @Size(min = 2, max = 50, message = "O nome do clube deve ser de, no mínimo, duas letras.")
    private String name;

    @Schema(description = "Sigla do estado (UF) do clube, com checagem da validade do inpupt",
            example = "SP")
    @Getter
    @NotBlank(message = "O estado do clube é mandatório para o cadastro")
    @Size(min = 2, max = 2, message = "O estado do clube deve ser composto por duas letras.")
    private String stateAcronym;

    @Schema(description = "Data de criação do clube", example = "2023-10-01")
    @Setter
    @Getter
    @PastOrPresent(message = "A data de criação do clube deve ser presente ou passada.")
    private LocalDate createdOn;

    @Schema(description = "Booleano que indica se o clube está ativo ou inativo. Padrão: true",
            example = "false")
    @Setter
    @Getter
    @Column(columnDefinition = "boolean default true")
    private Boolean isActive;

    public void setStateAcronym(String stateAcronym) {
        isValidBrazilianState(stateAcronym);
        this.stateAcronym = stateAcronym.toUpperCase();
    }
}
