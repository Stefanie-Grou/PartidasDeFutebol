package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.dto.AddressDTO;
import com.example.partidasdefutebol.exceptions.CustomException;
import org.springframework.web.client.RestTemplate;

public class AddressSearchService {
    public static AddressDTO findFullAddressByCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://viacep.com.br/ws/" + cep + "/json/";
        AddressDTO address = restTemplate.getForObject(url, AddressDTO.class);
        if (address.getCep() == null) {
            throw new CustomException("O CEP informado não é válido.", 409);
        }
    return address;
    }
}
