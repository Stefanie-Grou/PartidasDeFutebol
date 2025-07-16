package com.example.partidasdefutebol.entities;

import com.example.partidasdefutebol.exceptions.ConflictException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import static com.example.partidasdefutebol.enums.BrazilianStates.isValidBrazilianState;

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
    @Size(min = 2, max = 2, message = "Entre com a sigla do estado, composta por duas letras")
    private String stateAcronym;

    @Setter
    @Getter
    @PastOrPresent(message = "A data de criação do clube deve ser presente ou passada.")
    private LocalDate createdOn;

    @Setter
    @Getter
    private Boolean isActive;

    public void setStateAcronym(String stateAcronym) {
        if (isValidBrazilianState(stateAcronym)) {
            this.stateAcronym = stateAcronym;
        } else {
            throw new ConflictException("A sigla do estado é inválida.", 409);
        }
    }
}
