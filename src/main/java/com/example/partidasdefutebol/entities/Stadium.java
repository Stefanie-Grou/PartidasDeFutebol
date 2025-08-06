package com.example.partidasdefutebol.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = """
        Entidade Estádio.\s
        Registrado no banco de dados a partir do input na Controller, retornando por um DTO.
        Não permite o registro de um mesmo estádio para o mesmo estado. \n
        Utiliza a API ViaCep para obter o endereço completo a partir do CEP.""")
public class Stadium {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @Schema(description = "Identificador (Long) do estádio", example = "1 / 2 / 3")
    private Long id;

    @NotBlank
    @Getter
    @Setter
    @Schema(description = "Nome do estádio", example = "Cícero Pompeu de Toledo")
    private String name;

    @NotBlank
    @Getter
    @Setter
    @Schema(description = "Sigla do estado (UF) do estádio", example = "SP / RJ / SC / PR / RS / GO / DF")
    private String stateAcronym;

    @NotBlank
    @Getter
    @Setter
    @Schema(description = "Cidade do estádio", example = "São Paulo / Rio de Janeiro / Belo Horizonte")
    private String city;

    @Getter
    @Setter
    @Schema(description = "Logradouro (rua, avenida, etc.) do estádio", example = "Avenida Paulista")
    private String street;

    @Getter
    @Setter
    @NotBlank
    @Schema(description = "CEP do estádio, inserido pelo usuário na Controller", example = "12345678")
    private String cep;
}
