package com.example.partidasdefutebol.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class AddressEntity {
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
