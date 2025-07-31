package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.AddressEntity;
import com.example.partidasdefutebol.exceptions.CustomException;
import org.springframework.web.client.RestTemplate;

public class AddressSearchService {
    public static AddressEntity findFullAddressByCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://viacep.com.br/ws/" + cep + "/json/";
        AddressEntity address = restTemplate.getForObject(url, AddressEntity.class);
        if (address.getCep() == null) {
            throw new CustomException("O CEP informado não é válido.", 409);
        }
    return address;
    }
}
