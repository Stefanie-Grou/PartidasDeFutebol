package com.example.partidasdefutebol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class AddressDTO {
    @Getter
    @Setter
    @JsonProperty("uf")
    private String state;

    @Getter
    @Setter
    @JsonProperty("localidade")
    private String city;

    @Getter
    @Setter
    @JsonProperty("logradouro")
    private String street;

    @Getter
    @Setter
    private String cep;
}
