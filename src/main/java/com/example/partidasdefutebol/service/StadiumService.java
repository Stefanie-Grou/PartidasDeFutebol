package com.example.partidasdefutebol.service;

import com.example.partidasdefutebol.entities.StadiumEntity;
import com.example.partidasdefutebol.repository.StadiumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StadiumService {

    @Autowired
    private static StadiumRepository stadiumRepository;

    public StadiumEntity save(StadiumEntity stadiumEntity) {
        return stadiumRepository.save(stadiumEntity);
    }
}
