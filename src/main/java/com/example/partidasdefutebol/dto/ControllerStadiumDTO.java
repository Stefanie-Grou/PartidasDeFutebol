package com.example.partidasdefutebol.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO que captura o input do usuário para o registro de um estádio \n" +
        "Chama a API ViaCep para obter os dados do endereço completo e registrar no banco de dados.")
public class ControllerStadiumDTO {
    @NotBlank(message = "O nome do estadio é obrigatório")
    @Size(min = 3, max = 255, message = "O nome do estadio deve ser composto por, no mínimo, 3 letras.")
    @Getter
    @Setter
    @Schema(description = "Nome desejado para o registro do estádio", example = "Allianz Arena")
    private String stadiumName;

    @Getter
    @Setter
    @Digits(message = "O CEP do estádio deve ser composto por 8 números, sem traço.", integer = 8, fraction = 0)
    @NotBlank(message = "O CEP do estádio é mandatório para o cadastro")
    @Size(min = 8, max = 8, message = "O CEP do estádio deve ser composto por 8 números, sem traço.")
    @Schema(description = "CEP do estádio", example = "12345678")
    private String cep;
}
